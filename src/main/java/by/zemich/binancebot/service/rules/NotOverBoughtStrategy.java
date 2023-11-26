package by.zemich.binancebot.service.rules;

import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.service.api.IStrategy;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;

import java.math.BigDecimal;

public class NotOverBoughtStrategy extends TradeStrategy{
    @Override
    public String getName() {
        return "NOT_OVER_BOUGHT_STRATEGY";
    }

    @Override
    public BigDecimal getGoalPercentage() {
        return null;
    }

    @Override
    public EInterval getInterval() {
        return null;
    }

    @Override
    public IStrategy getAdditionalStrategy() {
        return null;
    }

    @Override
    protected Rule build(BarSeries series) {
        return null;
    }
}
