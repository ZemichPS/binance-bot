package by.zemich.binancebot.core.dto.binance;

import by.zemich.binancebot.core.enums.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class CancelOrderResponseDto {
    private String symbol;
    private String origClientOrderId;
    private Long orderId;
    private Long orderListId;
    private String clientOrderId;
    private Timestamp transactTime;
    private BigDecimal price;
    private BigDecimal origQty;
    private BigDecimal executedQty;
    private BigDecimal cummulativeQuoteQty;
    private EOrderStatus status;
    private ETimeInForce timeInForce;
    private EOrderType type;
    private ESide side;
    private String selfTradePreventionMode;

}
