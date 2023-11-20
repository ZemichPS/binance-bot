package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.core.dto.binance.AccountInformationQueryDto;
import by.zemich.binancebot.core.dto.binance.AccountInformationResponseDto;
import by.zemich.binancebot.core.dto.binance.AccountTradeQueryDto;
import by.zemich.binancebot.core.dto.binance.AccountTradeResponseDto;
import by.zemich.binancebot.service.api.IAccountService;
import by.zemich.binancebot.service.api.IConverter;
import com.binance.connector.client.SpotClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements IAccountService {

    private final IConverter converter;
    private final SpotClient spotClient;
    private final ObjectMapper objectMapper;

    public AccountServiceImpl(IConverter converter, SpotClient spotClient, ObjectMapper objectMapper) {
        this.converter = converter;
        this.spotClient = spotClient;
        this.objectMapper = objectMapper;
    }


    @Override
    public Optional<AccountInformationResponseDto> getInformation(AccountInformationQueryDto accountInformation) {

        String response = spotClient.createTrade().account(converter.dtoToMap(accountInformation));

        try {
            AccountInformationResponseDto information = objectMapper.readValue(response, AccountInformationResponseDto.class);
            return Optional.of(information);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<List<AccountTradeResponseDto>> getTradeList(AccountTradeQueryDto tradeQuery) {
        String response = spotClient.createTrade().myTrades(converter.dtoToMap(tradeQuery));
        try {

            List<AccountTradeResponseDto> accountTradeList = objectMapper.readValue(response, List.class);


            return Optional.of(accountTradeList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
