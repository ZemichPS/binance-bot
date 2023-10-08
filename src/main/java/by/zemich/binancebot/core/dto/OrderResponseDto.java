package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponseDto {
    private Integer accountId;
    private String clientOrderId;
    private BigDecimal cumQuote;
    private BigDecimal executedQty;
    private Long orderId;
    private BigDecimal origQty;
    private BigDecimal price;
    private ESide side;
    private EOrderStatus status;
    private BigDecimal stopPrice;
    private ESymbol symbol;
    private ETimeInForce timeInForce;
    private EOrderType type;
    private LocalDateTime updateTime;
}
