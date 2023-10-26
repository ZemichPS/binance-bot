package by.zemich.binancebot.core.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderBookTickerDto {
    private String symbol;
    private BigDecimal bidPrice;
    private BigDecimal bidQty;
    private BigDecimal askPrice;
    private BigDecimal askQty;
}
