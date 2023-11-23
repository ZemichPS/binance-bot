package by.zemich.binancebot.service.rules;

import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.bollinger.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.rules.*;


@Component
public class LowRsiAndclosePriceUnderBBL extends TradeRule {
    private final String name = "LOW_RSI_AND_CLOSE_PRICE_UNDER_BBL_RULE";


    @Override
    public String getName() {
        return name;
    }


    @Override
    protected Rule build(BarSeries series) {

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        OpenPriceIndicator openPriceIndicator = new OpenPriceIndicator(series);
        LowPriceIndicator lowPriceIndicator = new LowPriceIndicator(series);
        HighPriceIndicator highPriceIndicator = new HighPriceIndicator(series);


        EMAIndicator emaIndicator = new EMAIndicator(closePrice, 20);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, 14);



        // Standard deviation
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(emaIndicator);
        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd);
        BollingerBandWidthIndicator bbw = new BollingerBandWidthIndicator(bbu, bbm, bbl);



        return new UnderIndicatorRule(closePrice, bbl)
                .and(new UnderIndicatorRule(rsiIndicator, 30))
                .and(new OverIndicatorRule(bbw, 4))
                .and(new IsFallingRule(bbm, 30, 0.4))
                //   .and(new IsRisingRule(bbm, 20, 0.75))
                //      .and(new InPipeRule(rsiIndicator, 60, 45))
                //  .and(new IsRisingRule(balanceVolumeIndicator, 40, 0.2))
                //.and(new UnderIndicatorRule(adxIndicator, 45))
                .and(new NotRule(new OverIndicatorRule(highPriceIndicator, bbu)));


    }
}
