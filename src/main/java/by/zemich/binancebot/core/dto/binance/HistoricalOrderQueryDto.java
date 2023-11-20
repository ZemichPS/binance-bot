package by.zemich.binancebot.core.dto.binance;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class HistoricalOrderQueryDto {
    @NotEmpty(message = "symbol is mandatory")
    private String symbol;
    private Long orderId;
    private Long startTime;
    private Long endTime;
    private Integer limit;
    private Long recvWindow;

}
