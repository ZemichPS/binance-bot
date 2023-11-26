package by.zemich.binancebot.service.strategies;

import by.zemich.binancebot.service.api.IStrategy;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;

public abstract class TradeStrategy implements IStrategy {

    @Override
    public Rule getEnterRule(BarSeries series) {
        if (series == null) throw new RuntimeException("Bar series is required. Set not null series before.");
        return build(series);
    }
    protected abstract Rule build(BarSeries series);


}
