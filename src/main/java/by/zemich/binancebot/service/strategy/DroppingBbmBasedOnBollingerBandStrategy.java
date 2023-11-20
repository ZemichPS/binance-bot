package by.zemich.binancebot.service.strategy;

import by.zemich.binancebot.service.api.IStrategy;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.PPOIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.bollinger.*;
import org.ta4j.core.indicators.candles.RealBodyIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.*;


@Component
public class DroppingBbmBasedOnBollingerBandStrategy implements IStrategy {
    private final String name = "DROPPING_BBM_BOLLINGER_BAND_STRATEGY";
    private BarSeries series;
    private Strategy strategy;


    public Strategy get(BarSeries series) {
        this.series = series;
        return build();
    }

    public Strategy get() {
        if (series == null) throw new RuntimeException("Bar series is required. Set not null series before");
        return strategy;
    }

    @Override
    public String getName() {
        return name;
    }


    private Strategy build() {


        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        OpenPriceIndicator openPriceIndicator = new OpenPriceIndicator(series);
        LowPriceIndicator lowPriceIndicator = new LowPriceIndicator(series);
        HighPriceIndicator highPriceIndicator = new HighPriceIndicator(series);

        SMAIndicator longSma = new SMAIndicator(closePrice, 20);
        EMAIndicator emaIndicator = new EMAIndicator(closePrice, 20);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, 14);
        PercentBIndicator percentB = new PercentBIndicator(closePrice, 20, 2.0);
        OnBalanceVolumeIndicator balanceVolumeIndicator = new OnBalanceVolumeIndicator(series);

        RealBodyIndicator realBodyIndicator = new RealBodyIndicator(series);


        // Standard deviation
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(emaIndicator);
        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd);
        BollingerBandWidthIndicator bbw = new BollingerBandWidthIndicator(bbu, bbm, bbl);


        PPOIndicator ppoIndicator = new PPOIndicator(closePrice);
        ADXIndicator adxIndicator = new ADXIndicator(series, 14);


        Rule entryRule =
                new IsFallingRule(bbm,30,06);


        //
//        Rule entryRule =
//                new UnderIndicatorRule(openPriceIndicator, bbm)
//                        .and(new OverIndicatorRule(closePrice, bbm))
//                        .and(new OverIndicatorRule(bbw, 3.5))
//                        .and(new IsRisingRule(bbm, 30, 0.7))
//                        .and(new InPipeRule(rsiIndicator, 60, 45))
//                      //  .and(new IsRisingRule(balanceVolumeIndicator, 40, 0.2))
//                        //.and(new UnderIndicatorRule(adxIndicator, 45))
//                        .and(new NotRule(new OverIndicatorRule(highPriceIndicator, bbu)));



        Rule exitRule = new StopGainRule(closePrice, DecimalNum.valueOf("0.8"));

        strategy = new BaseStrategy(name, entryRule, exitRule);
        return strategy;
    }

}
