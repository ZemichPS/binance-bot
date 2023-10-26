package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.config.properties.KlineConfig;
import by.zemich.binancebot.core.dto.KlineQueryDto;
import by.zemich.binancebot.core.dto.NewOrderRequestDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.service.api.IStockMarketService;
import by.zemich.binancebot.service.api.IStrategyManager;
import by.zemich.binancebot.service.api.ITradeManager;
import by.zemich.binancebot.service.api.ITraderBot;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;
import org.ta4j.core.rules.StopGainRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
public class BinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final KlineConfig klineConfig;

    private final ITradeManager tradeManager;
    private final IStrategyManager strategyManager;

    private final Map<String, BarSeries> seriesMap = new HashMap<>();

    public BinanceTraderBotImpl(IStockMarketService stockMarketService, KlineConfig klineConfig, ITradeManager tradeManager, IStrategyManager strategyManager) {
        this.stockMarketService = stockMarketService;
        this.klineConfig = klineConfig;
        this.tradeManager = tradeManager;
        this.strategyManager = strategyManager;
    }

    @Override
    public void updateSeries() {
        KlineQueryDto queryDto = new KlineQueryDto();
        String timeFrame = klineConfig.getTimeFrame();
        queryDto.setLimit(klineConfig.getLimit());

        List<String> symbolsList = stockMarketService.getSpotSymbols().get();
        symbolsList.stream().forEach(symbol -> {
            queryDto.setSymbol(symbol);
            seriesMap.put(symbol, stockMarketService.getBarSeries(queryDto).orElse(null));
        });

        stockMarketService.getBarSeries(queryDto);

    }

    @Override
    public void lookForEnterPosition() {
        if (seriesMap.isEmpty()) return;

        seriesMap.entrySet().stream().forEach(stringBarSeriesEntry -> {
            BarSeries series = stringBarSeriesEntry.getValue();
            Strategy strategy = strategyManager.get(series);
            if (strategy.shouldEnter(series.getEndIndex())) {
                tradeManager.buy(new NewOrderRequestDto());
            }
        });

    }

    @Override
    public void lookForExitPosition() {
        if (strategyManager.get().getExitRule() == null) return;

        tradeManager.sell(new OrderDto(), strategyManager.get().getExitRule());

    }
}
