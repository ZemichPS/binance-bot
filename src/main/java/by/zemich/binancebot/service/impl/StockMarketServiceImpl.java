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
import org.ta4j.core.*;
import org.ta4j.core.num.DecimalNum;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

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
    public Optional<BarSeries> getBaseBars(KlineQueryDto klineQuery) {

        String result = spotClient.createMarket().klines(converter.dtoToMap(klineQuery));
        List<BarDto> barsList = stringResponseToListOfBarsDto(result);
        //Collections.reverse(barsList);

        BarSeries series = getCusomBarSeries(barsList);
        return Optional.of(series);
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

    private BarSeries getCusomBarSeries(List<BarDto> barDtoList) {
        BarSeries series = new BaseBarSeries("my_live_series");
        barDtoList.forEach(candle-> {
            ZonedDateTime closeTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(candle.getCloseTime().getTime()), ZoneId.of("Europe/Minsk"));
            ZonedDateTime openTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(candle.getCloseTime().getTime()), ZoneId.of("Europe/Minsk"));

            Bar bar = new BaseBar(
                    Duration.between(closeTime, openTime),
                    closeTime,
                    candle.getOpenPrice(),
                    candle.getHighPrice(),
                    candle.getLowPrice(),
                    candle.getClosePrice(),
                    candle.getVolume()
            );

            series.addBar(bar);

        });

        return series;
    }


    private List<BarDto> stringResponseToListOfBarsDto(String response) {
        List<BarDto> barsList = new ArrayList<>();

        List<Object> objectList = new JacksonJsonParser().parseList(response);

        objectList.stream().forEach(object -> {

            List<Object> rawCandleList = (List<Object>) object;

            Long openTime = Long.valueOf(rawCandleList.get(0).toString());
            Long closeTime = Long.valueOf(rawCandleList.get(6).toString());

            BarDto barDto = new BarDto();

            barDto.setOpenTime(new Timestamp(openTime));
            barDto.setOpenPrice(new BigDecimal(rawCandleList.get(1).toString()));
            barDto.setHighPrice(new BigDecimal(rawCandleList.get(2).toString()));
            barDto.setLowPrice(new BigDecimal(rawCandleList.get(3).toString()));
            barDto.setClosePrice(new BigDecimal(rawCandleList.get(4).toString()));
            barDto.setVolume(new BigDecimal(rawCandleList.get(5).toString()));
            barDto.setCloseTime(new Timestamp(closeTime));
            barDto.setQuoteAssetVolume(new BigDecimal(rawCandleList.get(7).toString()));
            barDto.setNumberOfTrades(Integer.parseInt(rawCandleList.get(8).toString()));
            barDto.setTakerBuyBaseAssetVolume(new BigDecimal(rawCandleList.get(9).toString()));
            barDto.setTakerBuyQuoteAssetVolume(new BigDecimal(rawCandleList.get(10).toString()));

            barsList.add(barDto);


        });
        return barsList;
    }

}


// BeanUtils.copyProperties();
