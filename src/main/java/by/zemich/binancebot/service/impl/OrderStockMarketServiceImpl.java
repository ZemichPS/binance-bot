package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.dto.binance.CancelOrderRequestDto;
import by.zemich.binancebot.core.dto.binance.CancelOrderResponseDto;
import by.zemich.binancebot.core.dto.binance.QueryOrderDto;
import by.zemich.binancebot.core.dto.binance.RequestForNewOrderDto;
import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.service.api.IOrderStockMarketService;
import org.springframework.stereotype.Service;

@Service
public class OrderStockMarketServiceImpl implements IOrderStockMarketService {
    @Override
    public OrderDto createOrder(RequestForNewOrderDto requestForNewOrderDto) {
        return null;
    }
    @Override
    public EOrderStatus getOrderStatus(QueryOrderDto queryOrder) {
        return null;
    }
    @Override
    public CancelOrderResponseDto cancelOrder(CancelOrderRequestDto cancelOrderRequestDto) {
        return null;
    }
}
