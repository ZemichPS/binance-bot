package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.core.dto.EventDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.service.api.IEventManager;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class EventManagerImpl implements IEventManager {
    @Override
    public EventDto get(EEventType eventType, OrderDto order) {
        EventDto eventDto = EventDto.builder().build();
        eventDto.setEventType(eventType);
        String eventText = MessageFormat.format("""
                        id: {0}
                        symbol: {1}
                        type: {2}
                        status: {3}
                        price: {4}
                        amount: {5}
                        """,
                order.getOrderId(),
                order.getSymbol(),
                order.getType(),
                order.getStatus(),
                order.getPrice(),
                order.getOrigQty());
        eventDto.setText(eventText);
        return eventDto;
    }
}
