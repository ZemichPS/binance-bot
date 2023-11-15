package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.*;

import java.util.List;
import java.util.Optional;

public interface ITradeManager {
    OrderDto createBuyLimitOrderByAskPrice(String symbol);
    OrderDto createBuyLimitOrderByBidPrice(String symbol);

    OrderDto createSellLimitOrder(Long orderId);

    OrderDto createStopLimitOrder(Long orderId);





}
