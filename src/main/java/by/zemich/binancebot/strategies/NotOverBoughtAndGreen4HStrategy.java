package by.zemich.binancebot.strategies;

import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.core.enums.EStrategyType;
import by.zemich.binancebot.service.api.IStrategy;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import java.math.BigDecimal;
import java.util.List;

public class NotOverBoughtAndGreen4HStrategy extends TradeStrategy {
    @Override
    public String getName() {
        return "NOT_OVER_BOUGHT_AND_GREEN_4H_ADDITIONAL_STRATEGY";
    }

    @Override
    public BigDecimal getInterest() {
        return null;
    }

    @Override
    public EInterval getInterval() {
        return EInterval.H4;
    }

    @Override
    public EStrategyType getStrategyType() {
        return EStrategyType.ADDITIONAL;
    }

    @Override
    public List<IStrategy> getAdditionalStrategy() {
        return null;
    }

    @Override
    protected Rule build(BarSeries series) {

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        OpenPriceIndicator openPrice = new OpenPriceIndicator(series);
        HighPriceIndicator highPrice = new HighPriceIndicator(series);
        SMAIndicator smaIndicator = new SMAIndicator(closePrice, 20);
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(smaIndicator);
        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, 14);

        return new UnderIndicatorRule(rsiIndicator, 75)
                .and(new OverIndicatorRule(closePrice, openPrice));
//                .and(new UnderIndicatorRule(closePrice, bbu))
//                .and(new UnderIndicatorRule(closePrice, highPrice));

     //   return new OverIndicatorRule(closePrice, openPriceIndicator);

    }
}
