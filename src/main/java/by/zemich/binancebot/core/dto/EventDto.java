package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.EEventType;
import lombok.Builder;
import lombok.Data;

import java.text.MessageFormat;

@Data
@Builder
public class EventDto {
    EEventType eventType;
    String text;

    public String toString() {
        return MessageFormat.format("""
                Event: {0},
                {1}
                """, prepareEventTypeToText(eventType), text);
    }

    private String prepareEventTypeToText(EEventType eventType){
        return eventType.toString().replace("_"," ");
    }
}
