package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.config.properties.RealTradeProperties;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.core.enums.*;
import by.zemich.binancebot.service.api.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


@Component
@Slf4j

public class TradeManagerImpl implements ITradeManager {

    private final IStockMarketService stockMarketService;
    private final IConverter converter;
    private final IOrderService orderService;
    private final INotifier notifier;
    private final IEventManager eventCreate;
    private final RealTradeProperties tradeProperties;
    private final IBargainService bargainService;
    private final List<String> blackList = new ArrayList<>(List.of("BUSDUSDT", "USDTUSDT"));

    private final ObjectMapper mapper;
    private final List<SymbolDto> symbolsList;

    public TradeManagerImpl(IStockMarketService stockMarketService,
                            IConverter converter,
                            IOrderService orderService,
                            INotifier notifier,
                            IEventManager eventCreate,
                            RealTradeProperties tradeProperties,
                            IBargainService bargainService, ObjectMapper mapper, List<SymbolDto> symbolsList) {
        this.stockMarketService = stockMarketService;
        this.converter = converter;
        this.orderService = orderService;
        this.notifier = notifier;
        this.eventCreate = eventCreate;
        this.tradeProperties = tradeProperties;
        this.bargainService = bargainService;
        this.mapper = mapper;
        this.symbolsList = symbolsList;
    }


    @Override
    public OrderDto createBuyLimitOrderByAskPrice(SymbolDto symbol) {
        BigDecimal askPrice = getAskPrice(symbol.getSymbol());
        BigDecimal quantity = tradeProperties.getDeposit().divide(askPrice, 1, RoundingMode.HALF_DOWN);

        NewOrderRequestDto newOrderRequest = NewOrderRequestDto.builder()
                .symbol(symbol.getSymbol())
                .price(askPrice)
                .side(ESide.BUY)
                .type(EOrderType.LIMIT)
                .quantity(quantity)
                .timeInForce(ETimeInForce.IOC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderEntity orderEntity = orderService.create(newOrderRequest).orElseThrow(RuntimeException::new);

        OrderDto orderDto = convertOrderEntityToDto(orderEntity);
        EventDto event = eventCreate.get(EEventType.BUY_LIMIT_ORDER, orderDto);
        notifier.notify(event);
        return orderDto;
    }

    @Override
    public OrderDto createBuyLimitOrderByBidPrice(SymbolDto symbol) {
        PriceBinanceFilter priceBinanceFilter = getPriceFilter(symbol);
        LotSizeBinanceFilter lotSizeBinanceFilter = getLotSizeFilter(symbol);

        BigDecimal stepSize = lotSizeBinanceFilter.getStepSize();

        BigDecimal currentPrice = getSymbolPrice(symbol.getSymbol());
        BigDecimal quantity = tradeProperties.getDeposit().divide(currentPrice, 10, RoundingMode.DOWN);
        BigDecimal computedQuantity = roundStepSize(quantity, stepSize).setScale(priceBinanceFilter.getTickSize().scale());

        NewOrderRequestDto newOrderRequest = NewOrderRequestDto.builder()
                .symbol(symbol.getSymbol())
                .price(currentPrice)
                .side(ESide.BUY)
                .type(EOrderType.LIMIT)
                .quantity(computedQuantity)
                .timeInForce(ETimeInForce.GTC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        log.info(newOrderRequest.toString());

        OrderEntity orderEntity = orderService.create(newOrderRequest).orElseThrow(RuntimeException::new);

        OrderDto orderDto = convertOrderEntityToDto(orderEntity);
        EventDto event = eventCreate.get(EEventType.BUY_LIMIT_ORDER, orderDto);
        notifier.notify(event);
        return orderDto;
    }

    @Override
    public OrderDto createSellLimitOrder(Long orderId) {


        OrderDto buyOrder = getOrderById(orderId);

        SymbolDto symbolDto = symbolsList.stream()
                .filter(symbol -> symbol.getSymbol().equals(buyOrder.getSymbol()))
                .findFirst()
                .orElseThrow();

        PriceBinanceFilter priceBinanceFilter = getPriceFilter(symbolDto);
        LotSizeBinanceFilter lotSizeBinanceFilter = getLotSizeFilter(symbolDto);

        String symbol = buyOrder.getSymbol();
        BigDecimal takerFee = tradeProperties.getTaker();

        BigDecimal interest = percent(buyOrder.getPrice(), tradeProperties.getGain());

        BigDecimal quantity = buyOrder.getOrigQty();
        BigDecimal stepSize = lotSizeBinanceFilter.getStepSize();
        BigDecimal sellQuantity = quantity.subtract(percent(quantity, takerFee));
        BigDecimal computedQuantity = roundStepSize(sellQuantity, stepSize).setScale(priceBinanceFilter.getTickSize().scale(), RoundingMode.UNNECESSARY);
        BigDecimal sellPrice = buyOrder.getPrice().add(interest);//.setScale(priceBinanceFilter.getTickSize().scale(), RoundingMode.HALF_UP);

        BigDecimal computingSellPrice = roundPriceStepSize(sellPrice, priceBinanceFilter.getTickSize()).setScale(priceBinanceFilter.getTickSize().scale(), RoundingMode.UNNECESSARY);


        NewOrderRequestDto newOrderRequest = NewOrderRequestDto.builder()
                .symbol(symbol)
                .price(computingSellPrice)
                .side(ESide.SELL)
                .type(EOrderType.LIMIT)
                .quantity(computedQuantity)
                .timeInForce(ETimeInForce.GTC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderEntity sellOrderEntity = orderService.create(newOrderRequest).orElseThrow(RuntimeException::new);

        OrderDto orderDto = convertOrderEntityToDto(sellOrderEntity);
        EventDto event = eventCreate.get(EEventType.BUY_LIMIT_ORDER, orderDto);
        notifier.notify(event);

        return orderDto;
    }

    @Override
    public OrderDto createStopLimitOrder(Long orderId) {

        OrderDto buyOrderDto = getOrderById(orderId);
        String symbol = buyOrderDto.getSymbol();


        BigDecimal gain = percent(buyOrderDto.getPrice(), tradeProperties.getGain());

        NewOrderRequestDto newOrderRequest = NewOrderRequestDto.builder()
                .symbol(symbol)
                .price(buyOrderDto.getPrice().add(gain))
                .stopPrice(buyOrderDto.getPrice().add(gain))
                .side(ESide.SELL)
                .type(EOrderType.TAKE_PROFIT_LIMIT)
                .quantity(buyOrderDto.getExecutedQty())
                .timeInForce(ETimeInForce.IOC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderEntity sellOrderEntity = orderService.create(newOrderRequest).orElseThrow(RuntimeException::new);


        return convertOrderEntityToDto(sellOrderEntity);
    }


    private BigDecimal getUSDTBalance() {
        AccountInformationQueryDto accountInformation = new AccountInformationQueryDto();
        AccountInformationResponseDto information = stockMarketService.getAccountInformation(converter.dtoToMap(accountInformation)).orElseThrow();
        return information.getBalances().stream()
                .filter(balanceDto -> balanceDto.getAsset().equals("USDT"))
                .findFirst()
                .orElseThrow()
                .getFree();
    }


    private BigDecimal getAskPrice(String symbol) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("symbol", symbol);
        OrderBookTickerDto tickerDto = stockMarketService.getOrderBookTicker(paramMap).orElseThrow(RuntimeException::new);
        return tickerDto.getAskPrice();
    }

    private BigDecimal getBidPrice(String symbol) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("symbol", symbol);
        OrderBookTickerDto tickerDto = stockMarketService.getOrderBookTicker(paramMap).orElseThrow(RuntimeException::new);
        return tickerDto.getBidPrice();
    }

    private OrderDto getOrderById(Long orderId) {
        OrderEntity orderEntity = orderService.getByOrderId(orderId).orElseThrow(RuntimeException::new);
        return convertOrderEntityToDto(orderEntity);
    }

    private OrderDto convertOrderEntityToDto(OrderEntity entity) {
        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(entity, orderDto);
        return orderDto;
    }

    private BigDecimal percent(BigDecimal value, BigDecimal percent) {
        return value.multiply(percent).divide(new BigDecimal(100), RoundingMode.DOWN);
    }

    private BigDecimal getSymbolPrice(String symbol) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", symbol);
        SymbolPriceTickerDto symbolPriceTicker = stockMarketService.getSymbolPriceTicker(params).orElseThrow();

        return symbolPriceTicker.getPrice();

    }

    private LotSizeBinanceFilter getLotSizeFilter(SymbolDto symbolDto){
        LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) symbolDto.getFilters().get(1);

        return LotSizeBinanceFilter.builder()
                .filterType(map.get("filterType"))
                .minQty(new BigDecimal(map.get("minQty")))
                .maxQty(new BigDecimal(map.get("maxQty")))
                .stepSize(new BigDecimal(map.get("stepSize")))
                .build();

    }


    private PriceBinanceFilter getPriceFilter(SymbolDto symbolDto){
        LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) symbolDto.getFilters().get(0);

        return PriceBinanceFilter.builder()
                .filterType(map.get("filterType"))
                .minPrice(new BigDecimal(map.get("minPrice")))
                .maxPrice(new BigDecimal(map.get("maxPrice")))
                .tickSize(new BigDecimal(map.get("tickSize")))
                .build();
    }

    private BigDecimal roundStepSize(BigDecimal quantity, BigDecimal stepSize){
        BigDecimal rest = quantity.remainder(stepSize);
        return quantity.subtract(rest);
    }

    private BigDecimal roundPriceStepSize(BigDecimal price, BigDecimal tickSize){
        BigDecimal rest = price.remainder(tickSize );
        return price.subtract(rest);
    }

}
