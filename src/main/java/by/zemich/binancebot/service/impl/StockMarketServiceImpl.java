package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.core.dto.BarDto;
import by.zemich.binancebot.core.dto.ExchangeInfoResponseDto;
import by.zemich.binancebot.core.dto.ExchangeInfoQueryDto;
import by.zemich.binancebot.core.dto.KlineQueryDto;
import by.zemich.binancebot.service.api.IConverter;
import by.zemich.binancebot.service.api.IStockMarketService;
import com.binance.connector.client.SpotClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.ta4j.core.BaseBar;

import java.util.List;
import java.util.Optional;

@Service
public class StockMarketServiceImpl implements IStockMarketService {
    private final SpotClient spotClient;
    private final IConverter converter;
    private final ConversionService conversionService;
    private final ObjectMapper objectMapper;

    public StockMarketServiceImpl(SpotClient spotClient, IConverter converter, ConversionService conversionService, ObjectMapper objectMapper) {
        this.spotClient = spotClient;
        this.converter = converter;
        this.conversionService = conversionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<List<BarDto>> getBars(KlineQueryDto klineQuery) {
        String result = spotClient.createMarket().klines(converter.dtoToMap(klineQuery));
        return Optional.of(conversionService.convert(result, List.class));

    }

    @Override
    public Optional<List<BaseBar>> getBaseBars(KlineQueryDto klineQuery) {
        String result = spotClient.createMarket().klines(converter.dtoToMap(klineQuery));
        return Optional.of(conversionService.convert(result, List<BaseBar>.class));
    }

    @Override
    public Optional<ExchangeInfoResponseDto> getExchangeInfo(ExchangeInfoQueryDto queryDto) {
        String result = spotClient.createMarket().exchangeInfo(converter.dtoToMap(queryDto));

        try {
            ExchangeInfoResponseDto exchangeInfo = objectMapper.readValue(result, ExchangeInfoResponseDto.class);
            return Optional.of(exchangeInfo);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(e);
        }
    }
}


// BeanUtils.copyProperties();
