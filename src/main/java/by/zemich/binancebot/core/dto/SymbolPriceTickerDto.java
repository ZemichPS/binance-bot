package by.zemich.binancebot.core.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SymbolPriceTickerDto {
    private String symbol;
    private BigDecimal price;
}
