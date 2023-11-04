package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.EEventType;
import lombok.Data;

@Data
public class EventDto {
    EEventType eventType;
    String text;
}
