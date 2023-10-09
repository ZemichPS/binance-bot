package by.zemich.binancebot.core.dto;

import lombok.Data;

import java.util.OptionalLong;

@Data
public class KlineQueryDto {
    private String symbol;
    private String interval;
    private Long startTime;
    private Long endTime;
    private Integer limit;

}
