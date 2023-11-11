package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.EEventType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDto {
    EEventType eventType;
    String text;
}
