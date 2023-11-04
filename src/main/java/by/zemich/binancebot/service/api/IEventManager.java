package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.EventDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EEventType;

public interface IEventManager {
    EventDto get(EEventType eventType, OrderDto order);
}
