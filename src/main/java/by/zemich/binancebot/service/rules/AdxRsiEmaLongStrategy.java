package by.zemich.binancebot.service.rules;

import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.service.api.IStrategy;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import java.math.BigDecimal;
//@Component
public class AdxRsiEmaLongStrategy extends TradeStrategy {
    @Override
    public String getName() {
        return "ADX_RSI_EMA_STRATEGY";
    }

    @Override
    public BigDecimal getGoalPercentage() {
        return new BigDecimal("1.0");
    }

    @Override
    public EInterval getInterval() {
        return EInterval.M15;
    }

    @Override
    public IStrategy getAdditionalStrategy() {
        return null;
    }

    @Override
    protected Rule build(BarSeries series) {

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        RSIIndicator rsi3Period = new RSIIndicator(closePrice, 3);
        ADXIndicator adxIndicator = new ADXIndicator(series, 5);
        EMAIndicator ema50Indicator = new EMAIndicator(closePrice, 50);

        return new OverIndicatorRule(closePrice, ema50Indicator)
                .and(new UnderIndicatorRule(rsi3Period, 20)
                        .and(new OverIndicatorRule(adxIndicator, 30)));
    }
}
