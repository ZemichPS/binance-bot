package by.zemich.binancebot.strategies;

import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.core.enums.EStrategyType;
import by.zemich.binancebot.service.api.IStrategy;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.adx.MinusDIIndicator;
import org.ta4j.core.indicators.adx.PlusDIIndicator;
import org.ta4j.core.indicators.bollinger.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.ChaikinMoneyFlowIndicator;
import org.ta4j.core.indicators.volume.NVIIndicator;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.rules.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class Main15mEnterStrategy extends TradeStrategy {

    private final String name = "MAIN_15M_ENTER_RULE";

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public BigDecimal getInterest() {
        return new BigDecimal("1");
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
        List<IStrategy> strategyList = new ArrayList<>();
        strategyList.add(new NotOverBoughtAndGreen4HStrategy());
        return strategyList;
    }

    @Override
    protected Rule build(BarSeries series) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        OpenPriceIndicator openPriceIndicator = new OpenPriceIndicator(series);
        LowPriceIndicator lowPriceIndicator = new LowPriceIndicator(series);
        HighPriceIndicator highPriceIndicator = new HighPriceIndicator(series);

        EMAIndicator emaIndicator = new EMAIndicator(closePrice, 20);
        SMAIndicator smaIndicator = new SMAIndicator(closePrice, 20);

        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, 14);

        // Standard deviation
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(smaIndicator);
        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd);
        BollingerBandWidthIndicator bbw = new BollingerBandWidthIndicator(bbu, bbm, bbl);

        ADXIndicator adxIndicator = new ADXIndicator(series, 20);
        MinusDIIndicator minusDIIndicator = new MinusDIIndicator(series, 20);
        PlusDIIndicator plusDIIndicator = new PlusDIIndicator(series, 20);

        OnBalanceVolumeIndicator obv = new OnBalanceVolumeIndicator(series);
        ChaikinMoneyFlowIndicator chaikinMoneyFlowIndicator = new ChaikinMoneyFlowIndicator(series, 20);

        NVIIndicator nviIndicator = new NVIIndicator(series);

        return new OverIndicatorRule(closePrice, bbm)

                .and(new OverIndicatorRule(bbw, 5))
                .and(new UnderIndicatorRule(rsiIndicator, 70))
                .and(new OverIndicatorRule(rsiIndicator, 45))
                // средняя (SMA) растёт
                .and(new IsRisingRule(bbm, 14, 0.75))
                .and(new IsRisingRule(obv, 3, 0.6))
                // Цена не достигала верхней границы Боллинджера
                .and(new UnderIndicatorRule(highPriceIndicator, bbu));



    }
}
