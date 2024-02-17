package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.core.dto.ta4j.BarDto;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.core.enums.ETimeInForce;
import by.zemich.binancebot.service.api.IConverter;
import by.zemich.binancebot.service.api.IEventManager;
import by.zemich.binancebot.service.api.INotifier;
import by.zemich.binancebot.service.api.IStockMarketService;
import by.zemich.binancebot.core.dto.binance.TickerSymbolShortQuery;
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
import java.util.stream.Collectors;

@Service
public class BinanceMarketServiceImpl implements IStockMarketService {
    private final SpotClient spotClient;
    private final IConverter converter;
    private final ConversionService conversionService;
    private final ObjectMapper objectMapper;
    private final INotifier notifier;
    private final IEventManager eventManager;


    public BinanceMarketServiceImpl(SpotClient spotClient, IConverter converter, ConversionService conversionService, ObjectMapper objectMapper, INotifier notifier, IEventManager eventManager) {
        this.spotClient = spotClient;
        this.converter = converter;
        this.conversionService = conversionService;
        this.objectMapper = objectMapper;
        this.notifier = notifier;
        this.eventManager = eventManager;
    }

    @Override
    public OrderDto createOrder(RequestForNewOrderDto requestForNewOrderDto) {
        String responseResult = spotClient.createTrade().newOrder(converter.dtoToMap(requestForNewOrderDto));

        try {
            NewOrderFullResponseDto orderFullResponseDto = objectMapper.readValue(responseResult, NewOrderFullResponseDto.class);
            OrderDto createdOrderDto = conversionService.convert(orderFullResponseDto, OrderDto.class);
            createdOrderDto.setUuid(UUID.randomUUID());
            return createdOrderDto;

        } catch (Exception exception) {
            EventDto eventDto = eventManager.get(EEventType.ERROR, exception);
            notifier.notify(eventDto);
            throw new RuntimeException(exception.getCause());
        }


    }

    @Override
    public BigDecimal getAskPriceForAsset(String assetSymbol) {
        return getAssetTicker(assetSymbol).getAskPrice();
    }

    @Override
    public BigDecimal getBidPriceForAsset(String assetSymbol) {
        return getAssetTicker(assetSymbol).getBidPrice();

    }

    @Override
    public BigDecimal getCurrentPriceForAsset(String assetSymbol) {

        String responseResult = spotClient.createMarket().tickerSymbol(getParamMapFromString("symbol", assetSymbol));
        try {
            SymbolPriceTickerDto ticker = objectMapper.readValue(responseResult, SymbolPriceTickerDto.class);
            return ticker.getPrice();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    }

    @Override
    public CancelOrderResponseDto cancelOrder(CancelOrderRequestDto cancelOrderRequestDto) {
        String responseResult = spotClient.createTrade().cancelOrder(converter.dtoToMap(cancelOrderRequestDto));
        try {
            CancelOrderResponseDto canceledOrder = objectMapper.readValue(responseResult, CancelOrderResponseDto.class);
            return canceledOrder;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }


    }

    @Override
    public Optional<List<BarDto>> getBars(KlineQueryDto klineQuery) {
        String result = spotClient.createMarket().klines(converter.dtoToMap(klineQuery));
        return Optional.of(stringResponseToListOfBarsDto(result));
    }

    @Override
    public EOrderStatus getOrderStatus(QueryOrderDto queryOrder) {
        try {
            String result = spotClient.createTrade().getOrders(converter.dtoToMap(queryOrder));
            List<EOrderStatus> statuses = this.convertStringResponseToOrderStatuses(result);
            if(statuses.isEmpty()) throw new RuntimeException("Failed to retrieve order status.");

            return statuses.get(0);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<BarSeries> getBarSeries(KlineQueryDto klineQuery) {

        String result = spotClient.createMarket().klines(converter.dtoToMap(klineQuery));
        List<BarDto> barsList = stringResponseToListOfBarsDto(result);
        BarSeries series = getCusomBarSeries(barsList, klineQuery.getSymbol());
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

    @Override
    public Optional<List<String>> getSpotSymbols() {
        ExchangeInfoQueryDto queryDto = new ExchangeInfoQueryDto();
        queryDto.setPermissions(new ArrayList<>(List.of("SPOT")));
        String result = spotClient.createMarket().exchangeInfo(converter.dtoToMap(queryDto));
        ExchangeInfoResponseDto exchangeInfo;
        try {
            exchangeInfo = objectMapper.readValue(result, ExchangeInfoResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<String> symbolsList = exchangeInfo.getSymbols().stream()
                .filter(symbolDto -> symbolDto.getStatus().equals("TRADING"))
                .filter(symbolDto -> symbolDto.getQuoteAsset().equals("USDT"))
                .map(Asset::getSymbol)
                .collect(Collectors.toList());

        return Optional.of(symbolsList);
    }

    @Override
    public Optional<List<Asset>> getSymbols() {
        ExchangeInfoQueryDto queryDto = new ExchangeInfoQueryDto();
        queryDto.setPermissions(new ArrayList<>(List.of("SPOT")));
        String result = spotClient.createMarket().exchangeInfo(converter.dtoToMap(queryDto));
        ExchangeInfoResponseDto exchangeInfo;
        try {
            exchangeInfo = objectMapper.readValue(result, ExchangeInfoResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(exchangeInfo.getSymbols());
    }

    @Override
    public Optional<List<HistoricalOrderResponseDto>> getHistoricalOrderList(Map<String, Object> params) {

        String responseResult = spotClient.createTrade().getOrders(converter.dtoToMap(params));
        try {
            List<HistoricalOrderResponseDto> historicalOrderResponses = objectMapper.readValue(responseResult, List.class);
            return Optional.of(historicalOrderResponses);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Optional<OrderBookTickerDto> getOrderBookTicker(Map<String, Object> params) {
        String responseResult = spotClient.createMarket().bookTicker(params);
        try {
            OrderBookTickerDto ticker = objectMapper.readValue(responseResult, OrderBookTickerDto.class);
            return Optional.of(ticker);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Optional<SymbolPriceTickerDto> getSymbolPriceTicker(Map<String, Object> params) {
        String responseResult = spotClient.createMarket().tickerSymbol(params);
        try {
            SymbolPriceTickerDto ticker = objectMapper.readValue(responseResult, SymbolPriceTickerDto.class);
            return Optional.of(ticker);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AccountInformationResponseDto> getAccountInformation(Map<String, Object> params) {
        String responseResult = spotClient.createTrade().account(params);
        try {
            AccountInformationResponseDto accountInformationResponse = objectMapper.readValue(responseResult, AccountInformationResponseDto.class);
            return Optional.of(accountInformationResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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


    private List<BarDto> stringResponseToListOfBarsDto(String response) {
        List<BarDto> barsList = new ArrayList<>();

        List<Object> objectList = new JacksonJsonParser().parseList(response);

        objectList.stream().forEach(object -> {

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

    private List<QueryOrderResponseDto> orderResponseConverter(String response) {
        List<QueryOrderResponseDto> symbols = new ArrayList<>();
        List<Object> objectList = new JacksonJsonParser().parseList(response);
        objectList.stream().forEach(object -> {

            LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) object;

            QueryOrderResponseDto orderResponse = new QueryOrderResponseDto();
            orderResponse.setSymbol(map.get("symbol").toString());
            orderResponse.setOrderId(Long.valueOf(map.get("orderId").toString()));
            orderResponse.setOrderListId(Long.valueOf(map.get("orderListId").toString()));
            orderResponse.setClientOrderId(map.get("clientOrderId").toString());
            orderResponse.setPrice(new BigDecimal(map.get("price").toString()));
            orderResponse.setOrigQty(new BigDecimal(map.get("origQty").toString()));
            orderResponse.setExecutedQty(new BigDecimal(map.get("executedQty").toString()));

            orderResponse.setCummulativeQuoteQty(new BigDecimal(map.get("cummulativeQuoteQty").toString()));
            orderResponse.setStatus(EOrderStatus.valueOf(map.get("status").toString()));
            orderResponse.setTimeInForce(ETimeInForce.valueOf(map.get("timeInForce").toString()));

            symbols.add(orderResponse);
        });

        return symbols;
    }

    private List<EOrderStatus> convertStringResponseToOrderStatuses(String response) {
        List<EOrderStatus> statuses = new ArrayList<>();
        List<Object> objectList = new JacksonJsonParser().parseList(response);
        objectList.stream().forEach(object -> {

            LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) object;
            QueryOrderResponseDto orderResponse = new QueryOrderResponseDto();
            EOrderStatus status = EOrderStatus.valueOf(map.get("status").toString());
            statuses.add(status);
        });

        return statuses;
    }

    private OrderBookTickerDto getAssetTicker(String assetSymbol) {
        String responseResult = spotClient.createMarket().bookTicker(getParamMapFromString("symbol", assetSymbol));
        try {
            return objectMapper.readValue(responseResult, OrderBookTickerDto.class);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Map<String, Object> getParamMapFromString(String paramName, Object paramValue) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(paramName, paramValue);
        return paramMap;
    }


}


