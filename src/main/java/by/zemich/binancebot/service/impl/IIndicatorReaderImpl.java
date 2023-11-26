package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.core.dto.IndicatorValuesDto;
import by.zemich.binancebot.service.api.IIndicatorReader;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandWidthIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.ChaikinMoneyFlowIndicator;
import org.ta4j.core.num.Num;
@Component
public class IIndicatorReaderImpl implements IIndicatorReader {

    @Override
    public IndicatorValuesDto getValues(BarSeries series) {

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        EMAIndicator emaIndicator = new EMAIndicator(closePrice, 20);

        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(emaIndicator);
        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, sd);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, sd);
        BollingerBandWidthIndicator bbw = new BollingerBandWidthIndicator(bbu, bbm, bbl);

        ADXIndicator adx20Indicator = new ADXIndicator(series, 20);
        ADXIndicator adx14Indicator = new ADXIndicator(series, 14);

        ChaikinMoneyFlowIndicator chaikinMoneyFlowIndicator = new ChaikinMoneyFlowIndicator(series, 20);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice, 14);

        Double emaSlope = slopeValue(series.getEndIndex(), 14, emaIndicator);


        return IndicatorValuesDto.builder()
                .emaSlope(emaSlope.toString())
                .bbw(bbw.getValue(series.getEndIndex()).toString())
                .rsi(rsiIndicator.getValue(series.getEndIndex()).toString())
                .adx14(adx14Indicator.getValue(series.getEndIndex()).toString())
                .adx20(adx20Indicator.getValue(series.getEndIndex()).toString())
                .cmf(chaikinMoneyFlowIndicator.getValue(series.getEndIndex()).toString())
                .build();
    }


    private double slopeValue(int index, int barCount, Indicator<Num> ref) {

        int count = 0;
        for (int i = Math.max(0, index - barCount + 1); i <= index; i++) {
            if (ref.getValue(i).isGreaterThan(ref.getValue(Math.max(0, i - 1)))) {
                count += 1;
            }
        }

        double ratio = count / (double) barCount;
        return ratio;
    }
}
