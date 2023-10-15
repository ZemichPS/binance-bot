package by.zemich.binancebot.core.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AccountTradeQueryDto {
    @NotEmpty(message = "Symbol is mandatory")
    private String symbol;
    private Long orderId;
    private Long startTime;
    private Long endTime;
    private Long fromId;
    private Integer limit;
    private Long recvWindow;

}
