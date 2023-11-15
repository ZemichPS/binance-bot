package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.EventDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.service.api.IEventManager;
import com.binance.connector.client.exceptions.BinanceClientException;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class EventManagerImpl implements IEventManager {
    @Override
    public EventDto get(EEventType eventType, OrderDto order) {
        EventDto eventDto = EventDto.builder().build();
        eventDto.setEventType(eventType);
        String eventText = MessageFormat.format("""
                        id: {0} \n
                        symbol: {1} \n
                        type: {2} \n
                        status: {3} \n
                        price: {4} \n
                        amount: {5} \n
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

    @Override
    public EventDto get(EEventType eventType, BinanceClientException binanceClientException) {

        String eventText = MessageFormat.format("""
                        Error: {0} \n
                        Cause: {1} \n
                        Error code: {3} \n
                        """,
                binanceClientException.getErrMsg(),
                binanceClientException.getCause().getMessage(),
                binanceClientException.getErrorCode());

        return EventDto.builder()
                .eventType(eventType)
                .text(eventText)
                .build();
    }

    @Override
    public EventDto get(EEventType eventType, BargainDto bargainDto) {
        String eventText = MessageFormat.format("""
                        Создана новая сделка: \n
                        Uuid: {1} \n
                        """,
                bargainDto.getUuid());

        return EventDto.builder()
                .eventType(eventType)
                .text(eventText)
                .build();
    }
}
