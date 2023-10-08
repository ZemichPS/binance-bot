package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.EOrderType;
import by.zemich.binancebot.core.enums.ESide;
import by.zemich.binancebot.core.enums.ESymbol;
import by.zemich.binancebot.core.enums.ETimeInForce;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDTO {
    private ESymbol symbol;
    private ESide side;
    private EOrderType orderType;
    private ETimeInForce timeInForce;
    private BigDecimal quantity;
    private BigDecimal price;
    private String newClientOrderId;
    private BigDecimal stopPrice;
    private Long recvWindow;
    private Long timestamp;



}
