package by.zemich.binancebot.service.impl.chein;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.MMAIndicator;
import org.ta4j.core.indicators.helpers.GainIndicator;
import org.ta4j.core.indicators.helpers.LossIndicator;
import org.ta4j.core.num.Num;

public class OpenedRSIIndicator extends CachedIndicator<Num> {
    private final MMAIndicator averageGainIndicator;
    private final MMAIndicator averageLossIndicator;

    public OpenedRSIIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator);
        this.averageGainIndicator = new MMAIndicator(new GainIndicator(indicator), barCount);
        this.averageLossIndicator = new MMAIndicator(new LossIndicator(indicator), barCount);
    }

    @Override
    public Num calculate(int index) {
        // compute relative strength
        Num averageGain = averageGainIndicator.getValue(index);
        Num averageLoss = averageLossIndicator.getValue(index);
        if (averageLoss.isZero()) {
            if (averageGain.isZero()) {
                return numOf(0);
            } else {
                return numOf(100);
            }
        }
        Num relativeStrength = averageGain.dividedBy(averageLoss);
        // compute relative strength index
        return numOf(100).minus(numOf(100).dividedBy(numOf(1).plus(relativeStrength)));
    }
}
