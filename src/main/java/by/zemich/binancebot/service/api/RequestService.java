package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.NewBuyOrderRequestDto;
import by.zemich.binancebot.core.dto.NewSellOrderByAskPriceRequestDto;
import by.zemich.binancebot.core.dto.NewSellOrderRequestDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.dto.binance.CancelOrderRequestDto;
import by.zemich.binancebot.core.dto.binance.RequestForNewOrderDto;

public interface RequestService {
    RequestForNewOrderDto getRequestForBuyLimitOrder(NewBuyOrderRequestDto buyRequest);
    RequestForNewOrderDto getRequestForSellLimitOrder(NewSellOrderRequestDto sellRequest);
    RequestForNewOrderDto getRequestForSellLimitOrderByAscPrice(NewSellOrderByAskPriceRequestDto request);
    CancelOrderRequestDto getRequestForCancelOrder(OrderDto order);
}
