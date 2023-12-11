package by.zemich.binancebot.core.dto.binance;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelOrderRequestDto {
    @NotEmpty(message = "symbol is mandatory")
    private String symbol;
    private Long orderId;
    private String origClientOrderId;
    private String newClientOrderId;
    private Long recvWindow;

}
