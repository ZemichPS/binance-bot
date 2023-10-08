package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CancelOrderResponseDto {
    private ESymbol symbol;
    private Long orderId;
    private String origClientOrderId;
    private String clientOrderId;
    private LocalDateTime transactTime;
    private BigDecimal price;
    private BigDecimal origQty;
    private BigDecimal executedQty;
    private BigDecimal cumQuote;
    private EOrderStatus status;
    private ETimeInForce timeInForce;
    private EOrderType type;
    private ESide side;

}
