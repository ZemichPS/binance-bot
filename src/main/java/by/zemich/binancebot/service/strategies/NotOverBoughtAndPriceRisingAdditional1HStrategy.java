package by.zemich.binancebot.service.strategies;

import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.service.api.IStrategy;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandWidthIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.rules.IsRisingRule;
import org.ta4j.core.rules.NotRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import java.math.BigDecimal;

public class NotOverBoughtAndPriceRisingAdditional1HStrategy extends TradeStrategy {
    @Override
    public String getName() {
        return "NOT_OVER_BOUGHT_AND_RISING_PRICE_STRATEGY";
    }

    @Override
    public BigDecimal getInterest() {
        return null;
    }

    @Override
    public EInterval getInterval() {
        return EInterval.H1;
    }

    @Override
    public IStrategy getAdditionalStrategy() {
        return null;
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

        OnBalanceVolumeIndicator obv = new OnBalanceVolumeIndicator(series);

        return new UnderIndicatorRule(rsiIndicator, 60)
                .and(new OverIndicatorRule(rsiIndicator, 38))
                .and(new OverIndicatorRule(closePrice, bbm))
                .and(new UnderIndicatorRule(openPriceIndicator, bbm))
                .and(new UnderIndicatorRule(highPriceIndicator, bbu));

    }
}
