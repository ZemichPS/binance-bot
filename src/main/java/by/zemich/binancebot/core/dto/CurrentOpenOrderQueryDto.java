package by.zemich.binancebot.core.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CurrentOpenOrderQueryDto {
    @NotEmpty(message = "Symbol is mandatory")
    private String symbol;
    private Long recvWindow;
}
