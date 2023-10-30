package by.zemich.binancebot.service.strategy;

import by.zemich.binancebot.service.api.IStrategyManager;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.PPOIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.*;


@Component
public class BollingerBasedSecondStrategy implements IStrategyManager {
    private final String name = "BollingerBandRisingMAStrategy";
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


    private Strategy build() {

        // ПРАВИЛО ВХОДА
        // 1. Close price ниже MA
        // 2. MA индикатор показывает уверенный рост
        // 3. RSI в туннеле от 47 до 53
        // 4 ВВ% <= 0.50
        // 5. OBV индикатор показывает уверенный рост


        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        SMAIndicator longSma = new SMAIndicator(closePrice, 20);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, 14);
        PercentBIndicator percentB = new PercentBIndicator(closePrice, 20, 2.0);
        OnBalanceVolumeIndicator balanceVolumeIndicator = new OnBalanceVolumeIndicator(series);


        // Standard deviation
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(longSma);
        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd);
        BollingerBandWidthIndicator bbw = new BollingerBandWidthIndicator(bbu, bbm, bbl);


        PPOIndicator ppoIndicator = new PPOIndicator(closePrice);


        // 1.
//        Rule entryRule = new UnderIndicatorRule(longSma, longSma.getValue(series.getEndIndex()))
//                // 2.
//                .and(new IsRisingRule(longSma, 14, 0.7))
//                // 3.
//                .and(new InPipeRule(rsiIndicator, 53, 47))
//                // 4.
//                .and(new UnderIndicatorRule(percentB, 0.50))
//                // 5.
//               .and(new IsRisingRule(balanceVolumeIndicator, 14, 0.1));
//
//        Rule exitRule = new StopGainRule(closePrice, DecimalNum.valueOf("0.8"));


        Rule entryRule = new UnderIndicatorRule(closePrice, bbm.getValue(series.getEndIndex()))
                // 2.
                .and(new IsRisingRule(bbm, 14, 1))
                // 3.
                .and(new InPipeRule(rsiIndicator, 53, 47))
                // 4.
                .and(new UnderIndicatorRule(percentB, 0.50))
                // 5.
                .and(new IsRisingRule(balanceVolumeIndicator, 20, 0.6));


        Rule exitRule = new StopGainRule(closePrice, DecimalNum.valueOf("0.8"));

        strategy = new BaseStrategy(name, entryRule, exitRule);
        return strategy;
    }

}
