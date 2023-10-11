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
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.ta4j.core.BaseBar;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return Optional.of(stringResponseToListOfBarsDto(result));
    }

    @Override
    public Optional<List<BaseBar>> getBaseBars(KlineQueryDto klineQuery) {
        String result = spotClient.createMarket().klines(converter.dtoToMap(klineQuery));
        List<BaseBar> baseBarList = new ArrayList<>();

        baseBarList = stringResponseToListOfBarsDto(result).stream()
                .map(barDto -> conversionService.convert(barDto, BaseBar.class))
                .collect(Collectors.toList());

        return Optional.of(baseBarList);
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

    private List<BarDto> stringResponseToListOfBarsDto(String response) {
        List<BarDto> barDtos = new ArrayList<>();
        List<Object> objectList = new JacksonJsonParser().parseList(response);

        objectList.stream().forEach(object -> {
            List<Object> rawCandleList = (List<Object>) object;
            Long openTimeTimestamp = Long.valueOf(rawCandleList.get(0).toString());
            Long closeTimeTimestamp = Long.valueOf(rawCandleList.get(6).toString());
            barDtos.add(
                    new BarDto(
                            new Timestamp(openTimeTimestamp),
                            new BigDecimal(rawCandleList.get(1).toString()),
                            new BigDecimal(rawCandleList.get(2).toString()),
                            new BigDecimal(rawCandleList.get(3).toString()),
                            new BigDecimal(rawCandleList.get(4).toString()),
                            new BigDecimal(rawCandleList.get(5).toString()),
                            new Timestamp(closeTimeTimestamp),
                            new BigDecimal(rawCandleList.get(7).toString()),
                            (Integer) rawCandleList.get(8),
                            new BigDecimal(rawCandleList.get(9).toString()),
                            new BigDecimal(rawCandleList.get(10).toString())
                    )
            );
        });
        return barDtos;
    }

}


// BeanUtils.copyProperties();
