package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.core.dto.ta4j.BarDto;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.core.exeption.AssetNotAvailableException;
import by.zemich.binancebot.service.api.AssetBrokerService;
import by.zemich.binancebot.service.api.Converter;
import com.binance.connector.client.SpotClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class AssetBrokerServiceImpl implements AssetBrokerService {
    private final SpotClient spotClient;
    private final ObjectMapper objectMapper;
    private final ModelMapper mapper;
    private final Converter converter;

    public AssetBrokerServiceImpl(SpotClient spotClient,
                                  ObjectMapper objectMapper,
                                  ModelMapper mapper,
                                  @Qualifier("reflectionConverter") Converter converter) {
        this.spotClient = spotClient;
        this.objectMapper = objectMapper;
        this.mapper = mapper;
        this.converter = converter;
    }


    @Override
    public Asset getBySymbol(String symbol) {
        return this.getAllAvailable().stream()
                .filter(asset -> asset.getSymbol().equals(symbol))
                .findFirst()
                .orElseThrow(() -> new AssetNotAvailableException(symbol));
    }

    @Override
    public List<Asset> getAllSpotTradingToUsdtSpotTrading() {
        return this.getAllAvailableForSpotTrading().stream()
                .filter(symbolDto -> symbolDto.getStatus().equals("TRADING"))
                .filter(symbolDto -> symbolDto.getQuoteAsset().equals("USDT"))
                .toList();
    }

    @Override
    public List<Asset> getAllAvailableForSpotTrading() {
        ExchangeInfoQueryDto queryDto = ExchangeInfoQueryDto.builder()
                .permissions(List.of("SPOT"))
                .build();
        String result = spotClient.createMarket().exchangeInfo(converter.dtoToMap(queryDto));
        ExchangeInfoResponseDto exchangeInfo;
        try {
            return objectMapper.readValue(result, ExchangeInfoResponseDto.class).getSymbols();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Asset> getAllAvailable() {
        ExchangeInfoQueryDto queryDto = ExchangeInfoQueryDto.builder()
                .build();
        String result = spotClient.createMarket().exchangeInfo(converter.dtoToMap(queryDto));
        ExchangeInfoResponseDto exchangeInfo;
        try {
            return objectMapper.readValue(result, ExchangeInfoResponseDto.class).getSymbols();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BigDecimal getAskPriceBySymbol(String assetSymbol) {
        return getAssetTicker(assetSymbol).getAskPrice();
    }

    @Override
    public BigDecimal getBidPrice(String assetSymbol) {
        return getAssetTicker(assetSymbol).getBidPrice();;
    }

    @Override
    public BigDecimal getCurrentPrice(String assetSymbol) {
        String responseResult = spotClient.createMarket().tickerSymbol(getParametrizedMapFromString("symbol", assetSymbol));
        try {
            SymbolPriceTickerDto ticker = objectMapper.readValue(responseResult, SymbolPriceTickerDto.class);
            return ticker.getPrice();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    }
    @Override
    public ExchangeInfoResponseDto getExchangeInfo(ExchangeInfoQueryDto queryDto) {
        String result = spotClient.createMarket().exchangeInfo(converter.dtoToMap(queryDto));
        try {
            ExchangeInfoResponseDto exchangeInfo = objectMapper.readValue(result, ExchangeInfoResponseDto.class);
            return exchangeInfo;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public OrderBookTickerDto getOrderBookTicker(Map<String, Object> params) {
        String responseResult = spotClient.createMarket().bookTicker(params);
        try {
            return objectMapper.readValue(responseResult, OrderBookTickerDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SymbolPriceTickerDto getSymbolPriceTicker(String symbol) {
        String responseResult = spotClient.createMarket().tickerSymbol(getParametrizedMapFromString("symbol", symbol));
        try {
            return objectMapper.readValue(responseResult, SymbolPriceTickerDto.class);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private Map<String, Object> getParametrizedMapFromString(String paramName, Object paramValue) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(paramName, paramValue);
        return paramMap;
    }

    private OrderBookTickerDto getAssetTicker(String assetSymbol) {
        String responseResult = spotClient.createMarket().bookTicker(getParametrizedMapFromString("symbol", assetSymbol));
        try {
            return objectMapper.readValue(responseResult, OrderBookTickerDto.class);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public List<BarDto> getBars(KlineQueryDto klineQuery) {
        String result = spotClient.createMarket().klines(converter.dtoToMap(klineQuery));
        return stringResponseToListOfBarsDto(result);
    }

    @Override
    public BarSeries getBarSeries(KlineQueryDto klineQuery) {
        String result = spotClient.createMarket().klines(converter.dtoToMap(klineQuery));
        List<BarDto> barsList = stringResponseToListOfBarsDto(result);
        return getCusomBarSeries(barsList, klineQuery.getSymbol());
    }

    private List<BarDto> stringResponseToListOfBarsDto(String response) {
        List<BarDto> barsList = new ArrayList<>();
        List<Object> objectList = new JacksonJsonParser().parseList(response);

        objectList.forEach(object -> {
            List<Object> rawCandleList = (List<Object>) object;

            long openTime = Long.parseLong(rawCandleList.get(0).toString());
            long closeTime = Long.parseLong(rawCandleList.get(6).toString());

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

    private BarSeries getCusomBarSeries(List<BarDto> barDtoList, String name) {
        BarSeries series = new BaseBarSeries(name);
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
