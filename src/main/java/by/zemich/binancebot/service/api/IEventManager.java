package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.EventDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EEventType;
import com.binance.connector.client.exceptions.BinanceClientException;

public interface IEventManager {
    EventDto get(EEventType eventType, OrderDto order);
    EventDto get(EEventType eventType, BinanceClientException binanceClientException);
    EventDto get(EEventType eventType, BargainDto bargainDto);
}
