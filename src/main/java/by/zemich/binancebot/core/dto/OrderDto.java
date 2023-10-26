package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.core.enums.EOrderType;
import by.zemich.binancebot.core.enums.ESide;
import by.zemich.binancebot.core.enums.ETimeInForce;
import lombok.Data;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
public class OrderDto {

    private UUID uuid;
    private Timestamp dtUpdate;
    private Timestamp dtCreate;
    private String symbol;
    private Long orderId;
    private Long orderListId;
    private String clientOrderId;
    private Timestamp transactTime;
    private BigDecimal price;
    private BigDecimal stopPrice;
    private BigDecimal origQty;
    private BigDecimal executedQty;
    private BigDecimal cummulativeQuoteQty;
    private EOrderStatus status;
    private ETimeInForce timeInForce;
    private EOrderType type;
    private ESide side;
    private boolean isWorking;
    private Timestamp workingTime;
    private String selfTradePreventionMode;
}
