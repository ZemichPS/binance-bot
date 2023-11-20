package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.config.properties.RealTradeProperties;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.core.enums.EOrderType;
import by.zemich.binancebot.core.enums.ESide;
import by.zemich.binancebot.core.enums.ETimeInForce;
import by.zemich.binancebot.service.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


@Component
public class TradeManagerImpl implements ITradeManager {

    private final IStockMarketService stockMarketService;
    private final IConverter converter;
    private final IOrderService orderService;
    private final INotifier notifier;
    private final IEventManager eventCreate;
    private final RealTradeProperties tradeProperties;
    private final IBargainService bargainService;
    private final List<String> blackList = new ArrayList<>(List.of("BUSDUSDT", "USDTUSDT"));

    public TradeManagerImpl(IStockMarketService stockMarketService,
                            IConverter converter,
                            IOrderService orderService,
                            INotifier notifier,
                            IEventManager eventCreate,
                            RealTradeProperties tradeProperties,
                            IBargainService bargainService) {
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
        BigDecimal quantity = tradeProperties.getDeposit().divide(askPrice, 1, RoundingMode.HALF_DOWN);

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
        BigDecimal quantity = tradeProperties.getDeposit().divide(bidPrice, 1, RoundingMode.HALF_DOWN);

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
        BigDecimal gainIncome = percent(oldOrderDto.getPrice(), tradeProperties.getGain());

        NewOrderRequestDto newOrderRequest = NewOrderRequestDto.builder()
                .symbol(symbol)
                .price(oldOrderDto.getPrice().add(gainIncome))
                .side(ESide.SELL)
                .type(EOrderType.LIMIT)
                .quantity(oldOrderDto.getExecutedQty())
                .timeInForce(ETimeInForce.GTC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderEntity sellOrderEntity = orderService.create(newOrderRequest).orElseThrow(RuntimeException::new);


        return convertOrderEntityToDto(sellOrderEntity);
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


}
