package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.config.properties.KlineConfig;
import by.zemich.binancebot.core.dto.Event;
import by.zemich.binancebot.core.dto.KlineQueryDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.service.api.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;

import java.util.HashMap;
import java.util.Map;

@Component
@EnableScheduling
@Async
@Log4j2
public class BinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final KlineConfig klineConfig;

    private final ITradeManager tradeManager;
    private final IStrategyManager strategyManager;
    private final INotifier notifier;

    private final Map<String, BarSeries> seriesMap = new HashMap<>();

    public BinanceTraderBotImpl(IStockMarketService stockMarketService, KlineConfig klineConfig, TestTradeManagerImpl tradeManager, IStrategyManager strategyManager, INotifier notifier) {
        this.stockMarketService = stockMarketService;
        this.klineConfig = klineConfig;
        this.tradeManager = tradeManager;
        this.strategyManager = strategyManager;
        this.notifier = notifier;
    }


    @Override
    @Scheduled(fixedDelay = 40_000, initialDelay = 1_000)
    @Async
    public void lookForEnterPosition() {

        KlineQueryDto queryDto = new KlineQueryDto();
        String timeFrame = klineConfig.getTimeFrame();
        queryDto.setLimit(klineConfig.getLimit());
        queryDto.setInterval(timeFrame);

        stockMarketService.getSpotSymbols().get().stream()
                .forEach(symbol -> {
                    queryDto.setSymbol(symbol);
                    BarSeries series = stockMarketService.getBarSeries(queryDto).orElse(null);
                    Strategy strategy = strategyManager.get(series);
                    if (strategy.shouldEnter(series.getEndIndex())) {
                        tradeManager.buy(symbol);
                        Event event = new Event();
                        event.setEventType(EEventType.ENDPOINT_WAS_FOUNDED);
                        event.setText(symbol);
                        notifier.notify(event);
                    }

                });
    }

    @Override
    public void lookForExitPosition() {
        if (strategyManager.get().getExitRule() == null) return;

        tradeManager.sell(new OrderDto(), strategyManager.get().getExitRule());

    }

    private void updateSeries() {

    }
}
