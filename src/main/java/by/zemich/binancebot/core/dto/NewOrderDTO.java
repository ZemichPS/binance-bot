package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.EOrderType;
import by.zemich.binancebot.core.enums.ESide;
import by.zemich.binancebot.core.enums.ETimeInForce;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewOrderDTO {
    @NotEmpty(message = "Symbol is mandatory")
    private String symbol;
    @NotEmpty(message = "Side is mandatory")
    private ESide side;
    @NotEmpty(message = "Type is mandatory")
    private EOrderType type;
    private ETimeInForce timeInForce;
    @NotEmpty(message = "Type is mandatory")
    @Min(value = 1)
    private BigDecimal quantity;
    private BigDecimal quoteOrderQty;
    @NotEmpty(message = "Price is mandatory")
    private BigDecimal price;
    private Long newClientOrderId;
    private BigDecimal stopPrice;
    private BigDecimal icebergQty;
    private Long trailingDelta;
    private ENewOrderRespType newOrderRespType;
    private Long recvWindow;

}
