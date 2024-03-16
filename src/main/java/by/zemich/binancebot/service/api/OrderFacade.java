package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.dto.binance.CancelOrderRequestDto;
import by.zemich.binancebot.core.dto.binance.RequestForNewOrderDto;
import by.zemich.binancebot.core.enums.EOrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderFacade {
    OrderDto createBuyLimitOrderByCurrentPrice(RequestForNewOrderDto requestForNewOrderDto);
    OrderDto createSellLimitOrder(RequestForNewOrderDto requestForNewOrderDto);
    OrderDto createSellLimitOrderByAskPrice(RequestForNewOrderDto requestForNewOrderDto);
    OrderDto createSellOrderByMarketPrice(RequestForNewOrderDto requestForNewSellOrder);
    OrderDto createStopLimitOrder(OrderDto order);
    OrderDto cancelOrder(CancelOrderRequestDto cancelOrderRequest);
    OrderDto updateStatus(OrderDto orderDto, EOrderStatus expectedStatus);
    List<OrderDto> getAllBySymbol(String symbol);
    OrderDto getByOrderId(Long orderId);
    OrderDto getByUuid(UUID uuid);
    OrderDto getBySymbolAndOrderId(String symbol, Long orderId);

}
