package by.zemich.binancebot.core.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryOrderDto {
    @NotEmpty(message = "symbol is mandatory")
    private String symbol;
    private Long orderId;
    private String origClientOrderId;
    private Long recvWindow;

}
