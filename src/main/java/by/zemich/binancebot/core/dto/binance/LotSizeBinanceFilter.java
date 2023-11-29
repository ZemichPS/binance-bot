package by.zemich.binancebot.core.dto.binance;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class LotSizeBinanceFilter extends BinanceFilter {
    private String filterType;
    private BigDecimal minQty;
    private BigDecimal maxQty;
    private BigDecimal stepSize;
}
