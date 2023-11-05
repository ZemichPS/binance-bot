package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EOrderType;

public interface ITradeManager {
    OrderDto createBuyLimitOrder(String symbol);

    OrderDto createSellLimitOrder(Long orderId);

    OrderDto createStopLimitOrder(Long orderId);

}
