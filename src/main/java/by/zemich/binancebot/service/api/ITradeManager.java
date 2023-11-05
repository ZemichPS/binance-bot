package by.zemich.binancebot.service.api;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.NewOrderRequestDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EOrderType;
import org.ta4j.core.Rule;

public interface ITradeManager {
    OrderDto buy(String symbol);

    OrderDto sell(Long orderId, EOrderType orderType);

}
