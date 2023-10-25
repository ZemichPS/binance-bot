package by.zemich.binancebot.core.dto;

import lombok.*;

import java.util.OptionalLong;

@Data
@Builder
@NoArgsConstructor
public class KlineQueryDto {
    private String symbol;
    private String interval;
    private Long startTime;
    private Long endTime;
    private Integer limit;

}
