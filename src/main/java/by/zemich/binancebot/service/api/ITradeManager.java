package by.zemich.binancebot.service.api;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.NewOrderRequestDto;
import by.zemich.binancebot.core.dto.OrderDto;

public interface ITradeManager {
    OrderEntity buy(NewOrderRequestDto newOrderRequest);

    OrderEntity sell(OrderDto orderDto);

}
