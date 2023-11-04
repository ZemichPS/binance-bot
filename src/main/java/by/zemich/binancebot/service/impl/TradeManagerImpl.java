package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.core.enums.EOrderType;
import by.zemich.binancebot.core.enums.ESide;
import by.zemich.binancebot.core.enums.ETimeInForce;
import by.zemich.binancebot.service.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.ta4j.core.Rule;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TradeManagerImpl implements ITradeManager {

    private final IStockMarketService stockMarketService;
    private final IConverter converter;
    private final IOrderService orderService;
    private final INotifier notifier;
    private final IEventManager eventCreate;

    private final BigDecimal deposit = new BigDecimal("20");

    public TradeManagerImpl(IStockMarketService stockMarketService,
                            IConverter converter,
                            IOrderService orderService, INotifier notifier, IEventManager eventCreate) {
        this.stockMarketService = stockMarketService;
        this.converter = converter;
        this.orderService = orderService;
        this.notifier = notifier;
        this.eventCreate = eventCreate;
    }


    @Override
    public OrderDto buy(String symbol) {

        BigDecimal amountForTrade = new BigDecimal("20");
        NewOrderRequestDto orderRequest = createLimitOrder(symbol, amountForTrade);
        OrderEntity orderEntity = orderService.create(orderRequest).orElseThrow(RuntimeException::new);

        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(orderEntity, orderDto);

        EventDto event = eventCreate.get(EEventType.BUYING, orderDto);
        notifier.notify(event);
        return orderDto;
    }

    @Override
    public OrderDto sell(OrderDto orderDto, Rule exitRule) {
        return null;
    }

    private NewOrderRequestDto createLimitOrder(String symbol, BigDecimal usdtAmount) {
        BigDecimal askPrice = getPrice(symbol);
        BigDecimal quantity = usdtAmount.divide(askPrice).setScale(2, RoundingMode.HALF_UP);

        NewOrderRequestDto newOrder = NewOrderRequestDto.builder()
                .symbol(symbol)
                .price(askPrice)
                .side(ESide.BUY)
                .type(EOrderType.LIMIT)
                .quantity(quantity)
                .timeInForce(ETimeInForce.IOC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        return newOrder;
    }

    private BigDecimal getPrice(String symbol) {
        OrderBookTickerDto tickerDto = stockMarketService.getOrderBookTicker(converter.dtoToMap(symbol)).orElseThrow(RuntimeException::new);
        return tickerDto.getAskPrice();
    }
}
