package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.core.enums.EOrderStatus;

import java.util.List;
import java.util.Map;

public interface OrderBrokerService {
    OrderDto createOrder(RequestForNewOrderDto requestForNewOrderDto);
    List<HistoricalOrderResponseDto> getHistoricalOrderList(Map<String, Object> params);
    EOrderStatus getOrderStatus(QueryOrderDto queryOrder);
    CancelOrderResponseDto cancelOrder (CancelOrderRequestDto cancelOrderRequestDto);

}
