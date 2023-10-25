package by.zemich.binancebot.service.strategy;

import by.zemich.binancebot.service.api.IStrategyManager;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.bollinger.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.StopGainRule;
import org.ta4j.core.rules.UnderIndicatorRule;

@Component
public class BollingerBasedOnIStrategy implements IStrategyManager {
    private final String name = "BollingerBandAndRsiBaseOnStrategy";
    private BarSeries series;
    private final ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
    private final RSIIndicator rsiIndicator = new RSIIndicator(closePrice, 14);
     private final PercentBIndicator percentB = new PercentBIndicator(closePrice, 20, 3.0);

    // Правило перепроданности по RSI
    private Rule underRsiRule = new UnderIndicatorRule(rsiIndicator, 30);
    // Правило пробития нижнего уровня BB
    private Rule underPercentB = new UnderIndicatorRule(percentB, 0);
    private Rule enterRule = underRsiRule.and(underPercentB);
    private Rule exitRule = new StopGainRule(closePrice, DecimalNum.valueOf("0.8"));

    private Strategy strategy = new BaseStrategy(name, enterRule, exitRule);

    public Strategy get(BarSeries series){
        if(series == null) throw new RuntimeException("Bar series is required. Set not null series before");
        this.series = series;
        return strategy;
    }

}
