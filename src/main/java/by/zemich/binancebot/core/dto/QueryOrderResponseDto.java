package by.zemich.binancebot.core.dto;
import by.zemich.binancebot.core.enums.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class QueryOrderResponseDto {
    private ESymbol symbol;
    private Long orderId;
    private String clientOrderId;
    private BigDecimal price;
    private BigDecimal origQty;
    private BigDecimal executedQty;
    private BigDecimal cumQuote;
    private EOrderStatus status;
    private ETimeInForce timeInForce;
    private EOrderType type;
    private ESide side;
    private BigDecimal stopPrice;
    private LocalDateTime time;
    private LocalDateTime updateTime;

}
