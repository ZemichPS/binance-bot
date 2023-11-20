package by.zemich.binancebot.core.dto.binance;

import by.zemich.binancebot.core.dto.binance.ENewOrderRespType;
import by.zemich.binancebot.core.enums.EOrderType;
import by.zemich.binancebot.core.enums.ESide;
import by.zemich.binancebot.core.enums.ETimeInForce;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class NewOrderRequestDto {
    @NotEmpty(message = "Symbol is mandatory")
    private String symbol;
    @NotEmpty(message = "Side is mandatory")
    private ESide side;
    @NotEmpty(message = "Type is mandatory")
    private EOrderType type;
    @NotEmpty(message = "Time in force is mandatory")
    private ETimeInForce timeInForce;

    @NotEmpty(message = "Quantity is mandatory")
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
