package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.EventDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EBargainStatus;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.service.api.IEventManager;
import com.binance.connector.client.exceptions.BinanceClientException;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.MessageFormat;

@Component
public class EventManagerImpl implements IEventManager {
    @Override
    public EventDto get(EEventType eventType, OrderDto order) {
        EventDto eventDto = EventDto.builder().build();
        eventDto.setEventType(eventType);
        String eventText = MessageFormat.format("""
                        id: {0}
                        type: {1} 
                        symbol: {2} 
                        status: {3} 
                        price: {4} 
                        amount: {5} 
                        """,
                order.getOrderId(),
                order.getType(),
                order.getSymbol(),
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
                        Error code: {1} \n
                        """,
                binanceClientException.getErrMsg(),
                binanceClientException.getErrorCode());

        return EventDto.builder()
                .eventType(eventType)
                .text(eventText)
                .build();
    }

    @Override
    public EventDto get(EEventType eventType, BargainDto bargainDto) {
        EBargainStatus status = bargainDto.getStatus();

        String eventText = MessageFormat.format("""
                        Uuid: {0}
                        asset: {1}
                        strategy: {2}
                        """,
                bargainDto.getUuid(),
                bargainDto.getSymbol(),
                bargainDto.getStrategy());


        if (status.equals(EBargainStatus.FINISHED) || status.equals(EBargainStatus.CANCELED_IN_THE_RED)) {

            String additionalText = MessageFormat.format("""
                            ------------------💸--------------------
                            Finance result: {0},
                            Percentage result: {1}
                            sell price: {2}
                            buy price: {3}
                            Duration: {4} m.
                            """,
                    bargainDto.getFinanceResult().setScale(5, RoundingMode.HALF_UP).toString(),
                    bargainDto.getPercentageResult().setScale(5, RoundingMode.HALF_UP).toString(),
                    bargainDto.getSellOrder().getPrice().setScale(8, RoundingMode.HALF_UP).toString(),
                    bargainDto.getBuyOrder().getPrice().setScale(8, RoundingMode.HALF_UP).toString(),
                    bargainDto.getTimeInWork());
            eventText = eventText + additionalText;
        }

        return EventDto.builder()
                .eventType(eventType)
                .text(eventText)
                .build();
    }

    @Override
    public EventDto get(EEventType eventType, Exception exception) {
        String eventText = MessageFormat.format("""
                Error message: {0}
                Full cause: {1}
                """, exception.getMessage(), exception.getCause().toString());

        return EventDto.builder()
                .eventType(eventType)
                .text(eventText)
                .build();
    }
}
