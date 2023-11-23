package by.zemich.binancebot.service.rules;

import by.zemich.binancebot.service.api.IRule;
import by.zemich.binancebot.service.api.ITraderBot;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;

public abstract class TradeRule implements IRule {

    @Override
    public Rule get(BarSeries series) {
        if (series == null) throw new RuntimeException("Bar series is required. Set not null series before.");
        return build(series);
    }
    protected abstract Rule build(BarSeries series);


}
