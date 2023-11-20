package by.zemich.binancebot.service.strategy;

import by.zemich.binancebot.service.api.IStrategy;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.StopGainRule;
import org.ta4j.core.rules.UnderIndicatorRule;


@Component
public class СandlestickUnderBBM implements IStrategy {
    private final String name = "BEAR_CANDLESTICK_UNDER_BBM";
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
        EMAIndicator emaIndicator = new EMAIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(emaIndicator);


       // RealBodyIndicator realBodyIndicator = new RealBodyIndicator(series);

        Rule entryRule = new UnderIndicatorRule(closePrice, bbm);
        Rule exitRule = new StopGainRule(closePrice, DecimalNum.valueOf("0.8"));
        strategy = new BaseStrategy(name, entryRule, exitRule);
        return strategy;
    }

}
