package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.EEventType;
import lombok.Data;

@Data
public class Event {
    EEventType eventType;
    String text;
}
