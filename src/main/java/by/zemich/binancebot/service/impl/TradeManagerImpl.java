package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.config.properties.TradeProperties;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.core.enums.EOrderType;
import by.zemich.binancebot.core.enums.ESide;
import by.zemich.binancebot.core.enums.ETimeInForce;
import by.zemich.binancebot.service.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Component
public class TradeManagerImpl implements ITradeManager {

    private final IStockMarketService stockMarketService;
    private final IConverter converter;
    private final IOrderService orderService;
    private final INotifier notifier;
    private final IEventManager eventCreate;
    private final TradeProperties tradeProperties;

    private final IBargainService bargainService;

    public TradeManagerImpl(IStockMarketService stockMarketService,
                            IConverter converter,
                            IOrderService orderService, INotifier notifier, IEventManager eventCreate, TradeProperties tradeProperties, IBargainService bargainService) {
        this.stockMarketService = stockMarketService;
        this.converter = converter;
        this.orderService = orderService;
        this.notifier = notifier;
        this.eventCreate = eventCreate;
        this.tradeProperties = tradeProperties;
        this.bargainService = bargainService;
    }


    @Override
    public OrderDto createBuyLimitOrderByAskPrice(String symbol) {
        BigDecimal askPrice = getAskPrice(symbol);
        BigDecimal quantity = tradeProperties.getDeposit().divide(askPrice, 0, RoundingMode.HALF_UP);

        NewOrderRequestDto newOrderRequest = NewOrderRequestDto.builder()
                .symbol(symbol)
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


    public OrderDto createBuyLimitOrderByBidPrice(String symbol) {
        BigDecimal bidPrice = getBidPrice(symbol);
        BigDecimal quantity = tradeProperties.getDeposit().divide(bidPrice, 0, RoundingMode.HALF_UP);

        NewOrderRequestDto newOrderRequest = NewOrderRequestDto.builder()
                .symbol(symbol)
                .price(bidPrice)
                .side(ESide.BUY)
                .type(EOrderType.LIMIT)
                .quantity(quantity)
                .timeInForce(ETimeInForce.GTC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderEntity orderEntity = orderService.create(newOrderRequest).orElseThrow(RuntimeException::new);

        OrderDto orderDto = convertOrderEntityToDto(orderEntity);
        EventDto event = eventCreate.get(EEventType.BUY_LIMIT_ORDER, orderDto);
        notifier.notify(event);
        return orderDto;
    }

    @Override
    public OrderDto createSellLimitOrder(Long orderId) {

        OrderDto oldOrderDto = getOrderById(orderId);

        String symbol = oldOrderDto.getSymbol();
        BigDecimal gain = percent(oldOrderDto.getPrice(), tradeProperties.getGain());

        NewOrderRequestDto newOrderRequest = NewOrderRequestDto.builder()
                .symbol(symbol)
                .price(oldOrderDto.getPrice().add(gain))
                .side(ESide.SELL)
                .type(EOrderType.LIMIT)
                .quantity(oldOrderDto.getExecutedQty())
                .timeInForce(ETimeInForce.GTC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderEntity sellOrderEntity = orderService.create(newOrderRequest).orElseThrow(RuntimeException::new);
        OrderDto sellOrderDto = convertOrderEntityToDto(sellOrderEntity);

        EventDto event = eventCreate.get(EEventType.SELL_LIMIT_ORDER, sellOrderDto);
        notifier.notify(event);

        return sellOrderDto;
    }

    @Override
    public OrderDto createStopLimitOrder(Long orderId) {

        OrderDto oldOrderDto = getOrderById(orderId);

        String symbol = oldOrderDto.getSymbol();
        BigDecimal gain = percent(oldOrderDto.getPrice(), tradeProperties.getGain());

        NewOrderRequestDto newOrderRequest = NewOrderRequestDto.builder()
                .symbol(symbol)
                .price(oldOrderDto.getPrice().add(gain))
                .stopPrice(oldOrderDto.getPrice().add(gain))
                .side(ESide.SELL)
                .type(EOrderType.STOP_LOSS_LIMIT)
                .quantity(oldOrderDto.getExecutedQty())
                .timeInForce(ETimeInForce.IOC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderEntity sellOrderEntity = orderService.create(newOrderRequest).orElseThrow(RuntimeException::new);
        OrderDto stopLimitOrder = convertOrderEntityToDto(sellOrderEntity);

        EventDto event = eventCreate.get(EEventType.STOP_LIMIT_ORDER, stopLimitOrder);
        notifier.notify(event);


        return stopLimitOrder;
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
}
