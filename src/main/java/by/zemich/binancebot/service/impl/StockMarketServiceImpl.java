package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.service.api.IConverter;
import by.zemich.binancebot.service.api.IStockMarketService;
import by.zemich.binancebot.core.dto.TickerSymbolShortQuery;
import com.binance.connector.client.SpotClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

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
    public Optional<BarSeries> getBarSeries(KlineQueryDto klineQuery) {

        String result = spotClient.createMarket().klines(converter.dtoToMap(klineQuery));
        List<BarDto> barsList = stringResponseToListOfBarsDto(result);


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

    @Override
    public Optional<List<SymbolShortDto>> getAllSymbols(TickerSymbolShortQuery query) {
        String result = spotClient.createMarket().tickerSymbol(converter.dtoToMap(query));

        List<SymbolShortDto> accountTradeList = symbolConverter(result);
        return Optional.ofNullable(accountTradeList);
    }

    private BarSeries getCusomBarSeries(List<BarDto> barDtoList) {
        BarSeries series = new BaseBarSeries("my_live_series");
        barDtoList.forEach(candle -> {
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

    private List<SymbolShortDto> symbolConverter(String response) {
        List<SymbolShortDto> symbols = new ArrayList<>();
        List<Object> objectList = new JacksonJsonParser().parseList(response);
        objectList.stream().forEach(object -> {

            LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) object;

            String symbol = map.get("symbol").toString();
            BigDecimal price = new BigDecimal(map.get("price").toString());
            SymbolShortDto symbolShort = new SymbolShortDto();
            symbolShort.setSymbol(symbol);
            symbolShort.setPrice(price);
            symbols.add(symbolShort);
        });

        return symbols;
    }

}


// BeanUtils.copyProperties();
