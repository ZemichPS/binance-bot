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
import org.ta4j.core.rules.*;

import java.math.BigDecimal;


//@Component
public class LowRsiAndClosePrice1HUnderBBL extends TradeStrategy {

    @Override
    public String getName() {
        return "LOW_RSI_AND_CLOSE_PRICE_UNDER_BBL_RULE";
    }

    @Override
    public BigDecimal getInterest() {
        return new BigDecimal("1.0");
    }

    @Override
    public EInterval getInterval() {
        return EInterval.H1;
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
        ChaikinMoneyFlowIndicator chaikinMoneyFlowIndicator = new ChaikinMoneyFlowIndicator(series, 20);

        return new UnderIndicatorRule(closePrice, bbl)
                .and(new UnderIndicatorRule(chaikinMoneyFlowIndicator, -15));


    }
}
