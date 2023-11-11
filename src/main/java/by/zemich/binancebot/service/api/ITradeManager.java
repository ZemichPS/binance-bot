package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.OrderDto;

public interface ITradeManager {
    OrderDto createBuyLimitOrderByAskPrice(String symbol);
    OrderDto createBuyLimitOrderByBidPrice(String symbol);

    OrderDto createSellLimitOrder(Long orderId);

    OrderDto createStopLimitOrder(Long orderId);



}
