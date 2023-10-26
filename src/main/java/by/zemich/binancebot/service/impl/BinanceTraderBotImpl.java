package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.config.properties.KlineConfig;
import by.zemich.binancebot.core.dto.KlineQueryDto;
import by.zemich.binancebot.core.dto.NewOrderRequestDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.service.api.IStockMarketService;
import by.zemich.binancebot.service.api.IStrategyManager;
import by.zemich.binancebot.service.api.ITradeManager;
import by.zemich.binancebot.service.api.ITraderBot;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;
import org.ta4j.core.rules.StopGainRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@Async
@Log4j2
public class BinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final KlineConfig klineConfig;

    private final ITradeManager tradeManager;
    private final IStrategyManager strategyManager;

    private final Map<String, BarSeries> seriesMap = new HashMap<>();

    public BinanceTraderBotImpl(IStockMarketService stockMarketService, KlineConfig klineConfig, TestTradeManagerImpl tradeManager, IStrategyManager strategyManager) {
        this.stockMarketService = stockMarketService;
        this.klineConfig = klineConfig;
        this.tradeManager = tradeManager;
        this.strategyManager = strategyManager;
    }

    @Override
    @Async
    @Scheduled(fixedDelay = 60_000, initialDelay = 2_000)
    public void updateSeries() {
        KlineQueryDto queryDto = new KlineQueryDto();
        String timeFrame = klineConfig.getTimeFrame();
        queryDto.setLimit(klineConfig.getLimit());
        queryDto.setInterval(timeFrame);

        stockMarketService.getSpotSymbols().get().stream()
                .forEach(symbol -> {
                    queryDto.setSymbol(symbol);
                    seriesMap.put(symbol, stockMarketService.getBarSeries(queryDto).orElse(null));
                });
    }

    @Override
    @Scheduled(fixedDelay = 60_000, initialDelay = 3_000)
    @Async
    public void lookForEnterPosition() {

        seriesMap.entrySet().stream().forEach(barSeriesEntry -> {
            BarSeries series = barSeriesEntry.getValue();
            String symbol = barSeriesEntry.getKey();
            Strategy strategy = strategyManager.get(series);
            if (strategy.shouldEnter(series.getEndIndex())) {
                tradeManager.buy(symbol);
            }
        });

    }

    @Override
    public void lookForExitPosition() {
        if (strategyManager.get().getExitRule() == null) return;

        tradeManager.sell(new OrderDto(), strategyManager.get().getExitRule());

    }
}
