package by.zemich.binancebot.core.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SymbolShortDto {
    private String symbol;
    private BigDecimal price;
}
