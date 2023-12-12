package by.zemich.binancebot.service.strategies;

import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.service.api.IStrategy;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandWidthIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.ChaikinMoneyFlowIndicator;
import org.ta4j.core.rules.IsRisingRule;
import org.ta4j.core.rules.NotRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import java.math.BigDecimal;

@Component
public class Bullish1HEnterStrategy extends TradeStrategy {
    private final String name = "BULLISH_1H_RULE";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BigDecimal getInterest() {
        return new BigDecimal("1.2");
    }

    @Override
    public EInterval getInterval() {
        return EInterval.M15;
    }

    @Override
    public IStrategy getAdditionalStrategy() {
        return new NotOverBoughtAndGreenCandleAdditional4HStrategy();
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
        ADXIndicator adxIndicator = new ADXIndicator(series, 7);

        ChaikinMoneyFlowIndicator chaikinMoneyFlowIndicator = new ChaikinMoneyFlowIndicator(series, 20);

        return new UnderIndicatorRule(lowPriceIndicator, bbm)
                .and(new OverIndicatorRule(openPriceIndicator, bbm))
                .and(new OverIndicatorRule(closePrice, openPriceIndicator))
                .and(new OverIndicatorRule(bbw, 6))
                .and(new UnderIndicatorRule(bbw, 14))
                .and(new IsRisingRule(bbm, 14, 0.7))
                .and(new UnderIndicatorRule(rsiIndicator, 65))
                .and(new OverIndicatorRule(rsiIndicator, 38))

                .and(new NotRule(new OverIndicatorRule(highPriceIndicator, bbu)));



    }
}
