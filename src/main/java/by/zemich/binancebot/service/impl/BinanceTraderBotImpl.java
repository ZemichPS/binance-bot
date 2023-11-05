package by.zemich.binancebot.service.impl;


import by.zemich.binancebot.core.dto.KlineQueryDto;
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
    private final ITradeManager tradeManager;
    private final INotifier notifier;
    private final BinanceMarketServiceImpl binanceMarketService;

    private final Map<String, IStrategy> strategyMap = new HashMap<>();

    public BinanceTraderBotImpl(IStockMarketService stockMarketService,
                                ITradeManager tradeManager, INotifier notifier,
                                BinanceMarketServiceImpl binanceMarketService) {
        this.stockMarketService = stockMarketService;
        this.tradeManager = tradeManager;
        this.notifier = notifier;
        this.binanceMarketService = binanceMarketService;
    }

    @Override
    public void registerStrategy(String name, IStrategy strategyManager) {
        strategyMap.put(name, strategyManager);
    }


    @Override
    @Scheduled(fixedDelay = 40_000, initialDelay = 1_000)
    @Async
    public void lookForEnterPosition() {

        KlineQueryDto queryDto = new KlineQueryDto();
        queryDto.setLimit(500);


        stockMarketService.getSpotSymbols().get().stream()
                .forEach(symbol -> {

                    queryDto.setSymbol(symbol);
                    queryDto.setInterval("15m");
                    BarSeries series = stockMarketService.getBarSeries(queryDto).orElse(null);
                    Strategy strategy = strategyMap.get("BOLLINGER_BAND_MAIN_STRATEGY").get(series);
                    if (strategy.shouldEnter(series.getEndIndex())) {
                        queryDto.setInterval("1h");
                        BarSeries secondSeries = stockMarketService.getBarSeries(queryDto).orElse(null);
                        Strategy sureStrategy = strategyMap.get("BOLLINGER_BAND_OLDER_TIMEFRAME_STRATEGY").get(secondSeries);
                       // if (sureStrategy.shouldEnter(secondSeries.getEndIndex())) {
                        if (true) {
                            tradeManager.createBuyLimitOrder(symbol);

                        }

                    }

                });
    }


}








