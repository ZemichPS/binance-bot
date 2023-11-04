package by.zemich.binancebot.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;

public interface IStrategy {
    Strategy get(BarSeries series);
    Strategy get();
    String getName();

    @Autowired
    default void regMe(ITraderBot traderBot){
        traderBot.registerStrategy(getName(), this);
    }


}
