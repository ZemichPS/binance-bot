package by.zemich.binancebot.service.strategy;

import by.zemich.binancebot.service.api.IStrategy;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandWidthIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.StopGainRule;
import org.ta4j.core.rules.UnderIndicatorRule;


@Component
public class BullishDailyСandlestick implements IStrategy {
    private final String name = "BULLISH_DAILY_CANDLESTICK";
    private BarSeries series;
    private Strategy strategy;


    public Strategy get(BarSeries series) {
        if (series == null) throw new RuntimeException("Bar series is required. Set not null series before");
        this.series = series;
        return build();
    }

    public Strategy get() {
        return strategy;
    }

    @Override
    public String getName() {
        return name;
    }


    private Strategy build() {

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        OpenPriceIndicator openPriceIndicator = new OpenPriceIndicator(series);
        Rule entryRule = new UnderIndicatorRule(openPriceIndicator, closePrice);
        Rule exitRule = new StopGainRule(closePrice, DecimalNum.valueOf("0.8"));
        strategy = new BaseStrategy(name, entryRule, exitRule);
        return strategy;
    }

}
