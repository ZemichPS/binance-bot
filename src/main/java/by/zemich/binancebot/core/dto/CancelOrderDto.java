package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.ESymbol;
import lombok.Data;

@Data
public class CancelOrderDto {
    private ESymbol symbol;
    private Long orderId;
    private String origClientOrderId;
    private Long recvWindow;
    private Long timestamp;

}
