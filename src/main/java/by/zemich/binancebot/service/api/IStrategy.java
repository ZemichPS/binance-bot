package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.enums.EInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;

import java.math.BigDecimal;

public interface IStrategy {
    Rule getEnterRule(BarSeries series);
    String getName();

    BigDecimal getGoalPercentage();

    EInterval getInterval();

    IStrategy getAdditionalStrategy();

    @Autowired
    default void regMe(ITraderBot traderBot){
        traderBot.registerStrategy(getName(), this);
    }


}