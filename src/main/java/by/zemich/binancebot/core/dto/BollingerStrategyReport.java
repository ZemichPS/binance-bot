package by.zemich.binancebot.core.dto;

import lombok.Builder;
import lombok.Data;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;


@Builder
@Data
public class BollingerStrategyReport {
    private BigDecimal percentBIndicatorValue;
    private BigDecimal bollingerBandWidthValue;
    private BigDecimal bollingerBandsUpperValue;
    private BigDecimal bollingerBandsMiddleValue;
    private BigDecimal bollingerBandsLowerValue;
    private BigDecimal rsiValue;
    private BigDecimal currentPriceValue;

}
