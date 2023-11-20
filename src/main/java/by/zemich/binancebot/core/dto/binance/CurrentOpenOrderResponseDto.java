package by.zemich.binancebot.core.dto.binance;

import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.core.enums.EOrderType;
import by.zemich.binancebot.core.enums.ESide;
import by.zemich.binancebot.core.enums.ETimeInForce;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class CurrentOpenOrderResponseDto {
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
