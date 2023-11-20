package by.zemich.binancebot.core.dto.binance;
import by.zemich.binancebot.core.enums.*;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class QueryOrderResponseDto {
    private String symbol;
    private Long orderId;
    private Long orderListId;
    private String clientOrderId;
    private BigDecimal price;
    private BigDecimal origQty;
    private BigDecimal executedQty;
    private BigDecimal cummulativeQuoteQty;
    private EOrderStatus status;
    private ETimeInForce timeInForce;
    private EOrderType type;
    private ESide side;
    private BigDecimal stopPrice;
    private BigDecimal icebergQty;
    private Timestamp time;
    private Timestamp updateTime;
    private boolean isWorking;
    private Timestamp workingTime;
    private BigDecimal origQuoteOrderQty;
    private String selfTradePreventionMode;

}
