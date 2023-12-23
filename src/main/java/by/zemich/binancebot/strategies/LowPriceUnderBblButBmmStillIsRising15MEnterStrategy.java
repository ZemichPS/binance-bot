package by.zemich.binancebot.strategies;

import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.core.enums.EStrategyType;
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
import org.ta4j.core.rules.*;

import java.math.BigDecimal;
import java.util.List;


//@Component
public class LowPriceUnderBblButBmmStillIsRising15MEnterStrategy extends TradeStrategy {

    @Override
    public String getName() {
        return "CLOSE_PRICE_UNDER_BBL_RULE_NOT_LOW_RSI";
    }

    @Override
    public BigDecimal getInterest() {
        return new BigDecimal("1.2");
    }

    @Override
    public EInterval getInterval() {
            return EInterval.M15;
    }

    @Override
    public EStrategyType getStrategyType() {
        return EStrategyType.BASIC;
    }

    @Override
    public List<IStrategy> getAdditionalStrategy() {
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
        ChaikinMoneyFlowIndicator chaikinMoneyFlowIndicator = new ChaikinMoneyFlowIndicator(series, 20);

        return new UnderIndicatorRule(lowPriceIndicator, bbl)
                .and(new OverIndicatorRule(openPriceIndicator, bbl))
                .and(new OverIndicatorRule(closePrice, openPriceIndicator))
                .and(new OverIndicatorRule(rsiIndicator, 40))
                .and(new IsRisingRule(bbm, 14, 0.5))
                .and(new OverIndicatorRule(bbw, 4));


    }
}
