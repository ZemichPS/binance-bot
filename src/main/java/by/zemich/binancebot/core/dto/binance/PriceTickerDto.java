package by.zemich.binancebot.core.dto.binance;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class PriceTickerDto {
    private String symbol;
    private BigDecimal price;

}
