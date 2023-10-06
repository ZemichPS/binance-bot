package by.zemich.binancebot.core.dto;


import by.zemich.binancebot.core.enums.ESymbol;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PriceTickerDto {
    private ESymbol symbol;
    private BigDecimal price;

}
