package by.zemich.binancebot.service.strategies;

import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.service.api.IStrategy;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.bollinger.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.ChaikinMoneyFlowIndicator;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.rules.*;

import java.math.BigDecimal;

@Component
public class Main15mEnterStrategy extends TradeStrategy {

    private final String name = "MAIN_15M_ENTER_RULE";

    @Override
    public String getName() {
        return this.name;
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
        ChaikinMoneyFlowIndicator chaikinMoneyFlowIndicator  = new ChaikinMoneyFlowIndicator(series, 20);

//        return new UnderIndicatorRule(openPriceIndicator, bbm)
//                .and(new OverIndicatorRule(closePrice, bbm))
//                .and(new IsRisingRule(bbm, 14, 0.6))
//                //   .and(new IsRisingRule(obv, 14, 0.1))
//                .and(new InPipeRule(rsiIndicator, 60, 45))
//                .and(new InPipeRule(bbw, 3.1, 5))
//                .and(new NotRule(new OverIndicatorRule(highPriceIndicator, bbu)));


        return new UnderIndicatorRule(openPriceIndicator, bbm)
                .and(new OverIndicatorRule(closePrice, bbm))
                .and(new IsRisingRule(bbm, 14, 0.4))
                .and(new InPipeRule(rsiIndicator, 60, 45))
                .and(new InPipeRule(bbw, 3.1, 6))
                .and(new OverIndicatorRule(chaikinMoneyFlowIndicator, 0))
                .and(new NotRule(new OverIndicatorRule(highPriceIndicator, bbu)));

    }
}