package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.NewOrderRequestDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.service.api.ITradeManager;
import org.springframework.stereotype.Component;
import org.ta4j.core.Rule;

@Component
public class TestTradeManagerImpl implements ITradeManager {
    @Override
    public OrderEntity buy(NewOrderRequestDto newOrderRequest) {
        System.out.println("Active was bought");
        System.out.println(newOrderRequest);

        return null;

    }

    @Override
    public OrderEntity sell(OrderDto orderDto, Rule exitRule) {

        System.out.println("Active was sell");

        return null;
    }
}
