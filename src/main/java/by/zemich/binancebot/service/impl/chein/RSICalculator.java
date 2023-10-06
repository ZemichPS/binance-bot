package by.zemich.binancebot.service.impl.chein;

import by.zemich.binancebot.core.dto.BarDto;

import java.util.List;
import java.util.Stack;

public class RSICalculator {
    private int periodLength;
    private final Stack<Averages> avgList;
    private final List<BarDto> prices;

    public RSICalculator(int periodLength, List<BarDto> bars) {

        this.periodLength = periodLength;
        this.avgList = new Stack<Averages>();
        this.prices = bars;
    }

    public double calculate() {
        double value = 0;
        int pricesSize = prices.size();
        int lastPrice = pricesSize - 1;
        int firstPrice = lastPrice - periodLength + 1;

        double gains = 0;
        double losses = 0;
        double avgUp = 0;
        double avgDown = 0;

        double delta = prices.get(lastPrice).closePrice().doubleValue()
                - prices.get(lastPrice - 1).closePrice().doubleValue();
        gains = Math.max(0, delta);
        losses = Math.max(0, -delta);

        if (avgList.isEmpty()) {
            for (int bar = firstPrice + 1; bar <= lastPrice; bar++) {
                double change = prices.get(bar).closePrice().doubleValue()
                        - prices.get(bar - 1).closePrice().doubleValue();
                gains += Math.max(0, change);
                losses += Math.max(0, -change);
            }
            avgUp = gains / periodLength;
            avgDown = losses / periodLength;
            avgList.push(new Averages(avgUp, avgDown));

        } else {

            Averages avg = avgList.pop();
            avgUp = avg.getAvgUp();
            avgDown = avg.getAvgDown();
            avgUp = ((avgUp * (periodLength - 1)) + gains) / (periodLength);
            avgDown = ((avgDown * (periodLength - 1)) + losses)
                    / (periodLength);
            avgList.add(new Averages(avgUp, avgDown));
        }
        value = 100 - (100 / (1 + (avgUp / avgDown)));

        return Math.round(value);
    }

    private class Averages {

        private final double avgUp;
        private final double avgDown;

        public Averages(double up, double down) {
            this.avgDown = down;
            this.avgUp = up;
        }

        public double getAvgUp() {
            return avgUp;
        }

        public double getAvgDown() {
            return avgDown;
        }
    }

    public int getPeriodLength() {
        return periodLength;
    }
}