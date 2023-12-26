package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.dto.binance.CancelOrderRequestDto;
import by.zemich.binancebot.core.dto.binance.CancelOrderResponseDto;
import by.zemich.binancebot.core.dto.binance.QueryOrderDto;
import by.zemich.binancebot.core.dto.binance.RequestForNewOrderDto;
import by.zemich.binancebot.core.enums.EOrderStatus;

import java.math.BigDecimal;

public interface IOrderStockMarketService {
    OrderDto createOrder(RequestForNewOrderDto requestForNewOrderDto);
    EOrderStatus getOrderStatus(QueryOrderDto queryOrder);
    CancelOrderResponseDto cancelOrder (CancelOrderRequestDto cancelOrderRequestDto);

}
