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
import org.ta4j.core.indicators.candles.RealBodyIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.*;


@Component
public class BasedOnBollingerBandOlderTimeframeStrategy implements IStrategy {
    private final String name = "BOLLINGER_BAND_OLDER_TIMEFRAME_STRATEGY";
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
        EMAIndicator emaIndicator = new EMAIndicator(closePrice, 20);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, 14);
        ADXIndicator adxIndicator = new ADXIndicator(series, 14);
//        PercentBIndicator percentB = new PercentBIndicator(closePrice, 20, 2.0);
//        OnBalanceVolumeIndicator balanceVolumeIndicator = new OnBalanceVolumeIndicator(series);


        // Standard deviation
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(emaIndicator);
        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd);
        BollingerBandWidthIndicator bbw = new BollingerBandWidthIndicator(bbu, bbm, bbl);

        HighPriceIndicator highPriceIndicator = new HighPriceIndicator(series);

//        PPOIndicator ppoIndicator = new PPOIndicator(closePrice);
//        LowPriceIndicator lowPriceIndicator = new LowPriceIndicator(series);

        RealBodyIndicator realBodyIndicator = new RealBodyIndicator(series);

        Rule entryRule = new UnderIndicatorRule(rsiIndicator, 72)
            //    .and(new OverIndicatorRule(realBodyIndicator, 0.00000000000000000));
             .and(new IsRisingRule(bbm, 12, 0.5));
        //   .and(new NotRule(new OverIndicatorRule(highPriceIndicator, bbu)));

        //          .and(new UnderIndicatorRule(adxIndicator, 40));
        Rule exitRule = new StopGainRule(closePrice, DecimalNum.valueOf("0.8"));

        strategy = new BaseStrategy(name, entryRule, exitRule);
        return strategy;
    }

}
