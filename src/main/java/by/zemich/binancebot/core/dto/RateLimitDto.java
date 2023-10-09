package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.ERateLimitType;
import lombok.Data;

@Data
public class RateLimitDto {
    private ERateLimitType rateLimitType;
    private String interval;
    private Integer intervalNum;
    private Integer limit;


}
