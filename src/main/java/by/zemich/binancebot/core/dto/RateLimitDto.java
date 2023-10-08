package by.zemich.binancebot.core.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

@Data
public class RateLimitDto {
    private ERateLimitType rateLimitType;
    private String interval;
    private Integer intervalNum;
    private Integer limit;


}
