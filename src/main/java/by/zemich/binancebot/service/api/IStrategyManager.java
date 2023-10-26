package by.zemich.binancebot.service.api;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;

public interface IStrategyManager {
    Strategy get (BarSeries series);
    Strategy get ();
}
