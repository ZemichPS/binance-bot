package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.IndicatorValuesDto;
import org.ta4j.core.BarSeries;

public interface IIndicatorReader {
    IndicatorValuesDto getValues(BarSeries series);
}
