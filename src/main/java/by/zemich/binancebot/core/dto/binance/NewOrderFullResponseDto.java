package by.zemich.binancebot.core.dto.binance;

import by.zemich.binancebot.core.enums.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class NewOrderFullResponseDto {

    private String symbol;
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
    private Timestamp workingTime;
    private String selfTradePreventionMode;
    private List<FillsDto> fills;


}
