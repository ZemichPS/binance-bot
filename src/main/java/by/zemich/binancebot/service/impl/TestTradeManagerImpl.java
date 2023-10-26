package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.NewOrderRequestDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.service.api.ITradeManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.ta4j.core.Rule;

import java.util.logging.Logger;

@Component
@Log4j2
public class TestTradeManagerImpl implements ITradeManager {


    @Override
    public OrderDto buy(String symbol) {
        System.out.println("Active was bought");
        System.out.println(symbol);

        log.info("Active was bought");
        log.info(symbol);
        return null;

    }

    @Override
    public OrderDto sell(OrderDto orderDto, Rule exitRule) {

        System.out.println("Active was sell");

        return null;
    }
}
