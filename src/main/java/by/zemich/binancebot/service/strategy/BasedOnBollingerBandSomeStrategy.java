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
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.*;


@Component
public class BasedOnBollingerBandSomeStrategy implements IStrategy {
    private final String name = "BOLLINGER_BAND_SOME_STRATEGY";
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


        // Standard deviation
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(emaIndicator);
        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd);
        BollingerBandWidthIndicator bbw = new BollingerBandWidthIndicator(bbu, bbm, bbl);


        PPOIndicator ppoIndicator = new PPOIndicator(closePrice);
        ADXIndicator adxIndicator = new ADXIndicator(series, 14);

        Rule entryRule =
                new UnderIndicatorRule(lowPriceIndicator, bbm)   //  ЦЕНА ХОДИЛА под bbm
                        .and(new OverIndicatorRule(closePrice, openPriceIndicator) // БЫЧЬЯ СВЕЧА
                                .and(new OverIndicatorRule(openPriceIndicator, bbm)) //
                                .and(new OverIndicatorRule(bbw, 3.0)) // ШИРИНА ПОЛОС БОЛЛИНДЖЕРА ДАВОЛЬНА ВЫСОКА
                                .and(new IsRisingRule(bbm, 14, 0.7)) // СРЕДНЯЯ РАСТЁТ
                                .and(new InPipeRule(rsiIndicator, 65, 45)) // RSI В КАНАЛЕ
                                .and(new IsRisingRule(balanceVolumeIndicator, 20, 0.3))); // ОБЪЁМЫ РАСТУТ
        //.and(new UnderIndicatorRule(adxIndicator, 40)); // ADX


        Rule exitRule = new StopGainRule(closePrice, DecimalNum.valueOf("0.8"));

        strategy = new BaseStrategy(name, entryRule, exitRule);
        return strategy;
    }

}
