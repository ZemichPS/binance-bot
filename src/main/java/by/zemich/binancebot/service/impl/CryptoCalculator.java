package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.core.dto.ta4j.BarDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@Component
public class CryptoCalculator {

    // РАЗНИЦА ЦЕНЫ НАБОРА СВЕЧЕЙ
    public BigDecimal getPriceChange(List<BarDto> bars) {

        BigDecimal result = bars.get(0).getClosePrice()
                .subtract(bars.get(bars.size() - 1).getClosePrice(), MathContext.UNLIMITED);
        return result;
    }

    // РАЗНИЦА ЦЕНЫ НАБОРА СВЕЧЕЙ С ОПРЕДЕЛЁННЫМ ИНТЕРВАЛОМ
    public BigDecimal getPriceChange(List<BarDto> bars, int interval) {
        if (interval > bars.size()) {
            throw new IllegalArgumentException("interval cannot be larger then bar set count.");
        }

        int offset = bars.size() - interval;
        BigDecimal result = bars.get(offset).getClosePrice()
                .subtract(bars.get(bars.size() - 1).getClosePrice(), MathContext.UNLIMITED);
        return result;
    }
    // РАЗНИЦА ЦЕНЫ НАБОРА СВЕЧЕЙ В ПРОЦЕНТАХ С ОПРЕДЕЛЁННЫМ ИНТЕРВАЛОМ
    public BigDecimal getPercentDifference(List<BarDto> bars, int interval) {

        if (interval > bars.size()) {
            throw new IllegalArgumentException("interval cannot be larger then bar set count.");
        }

        int offset = bars.size() - interval;
        BigDecimal firstBarHighPrice = bars.get(offset).getOpenPrice();
        BigDecimal currentPrice = bars.get(bars.size() - 1).getClosePrice();

        BigDecimal difference = currentPrice.subtract(firstBarHighPrice);

        BigDecimal resultPercent = difference.multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                .divide(currentPrice, 2, RoundingMode.HALF_UP);
        return resultPercent;
    }

    // РАЗНИЦА ЦЕНЫ НАБОРА СВЕЧЕЙ В ПРОЦЕНТАХ
    public BigDecimal getPercentDifference(List<BarDto> bars) {

        BigDecimal firstBarHighPrice = bars.get(0).getOpenPrice();
        BigDecimal currentPrice = bars.get(bars.size() - 1).getClosePrice();

        BigDecimal difference = currentPrice.subtract(firstBarHighPrice);

        BigDecimal resultPercent = difference.multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                .divide(currentPrice, 2, RoundingMode.HALF_UP);

        return resultPercent;
    }


    // РАЗНИЦА ЦЕНЫ В ПРОЦЕНТАХ
    public BigDecimal getPercentDifference(BigDecimal firstBarPrice, BigDecimal secondBarPrice) {

        BigDecimal difference = secondBarPrice.subtract(firstBarPrice);

        BigDecimal resultPercent = difference.multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                .divide(secondBarPrice, 2, RoundingMode.HALF_UP);

        return resultPercent;
    }



}
