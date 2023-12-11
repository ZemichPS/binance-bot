package by.zemich.binancebot.core.dto.binance;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PriceFilter extends BinanceFilter {
    private String filterType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal tickSize;

}
