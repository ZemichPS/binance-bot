package by.zemich.binancebot.core.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CancelOrderRequestDto {
    @NotEmpty(message = "symbol is mandatory")
    private String symbol;
    private Long orderId;
    private String origClientOrderId;
    private String newClientOrderId;
    private Long recvWindow;

}
