package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.SymbolDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITradeManager {
    OrderDto createBuyLimitOrderByAskPrice(SymbolDto symbol);
    OrderDto createBuyLimitOrderByBidPrice(SymbolDto symbol);

    OrderDto createSellLimitOrder(Long orderId);

    OrderDto createStopLimitOrder(Long orderId);





}
