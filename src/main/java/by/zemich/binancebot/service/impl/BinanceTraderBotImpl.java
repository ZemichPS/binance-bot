package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.api.IFakeOrderDao;
import by.zemich.binancebot.DAO.entity.FakeOrderEntity;
import by.zemich.binancebot.config.properties.KlineConfig;
import by.zemich.binancebot.core.dto.Event;
import by.zemich.binancebot.core.dto.KlineQueryDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.dto.SymbolPriceTickerDto;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.service.api.*;
import by.zemich.binancebot.service.strategy.BollingerBasedOlderTimeFrameStrategy;
import by.zemich.binancebot.service.strategy.BollingerBasedSecondStrategy;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
@Async
@Log4j2
public class BinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final KlineConfig klineConfig;
    private final ITradeManager tradeManager;
    private final BollingerBasedSecondStrategy secondStrategy;
    private final BollingerBasedOlderTimeFrameStrategy olderTimeFrameStrategy;
    private final INotifier notifier;

    private final IFakeOrderDao fakeOrderDao;

    private final BinanceMarketServiceImpl binanceMarketService;


    public BinanceTraderBotImpl(IStockMarketService stockMarketService, KlineConfig klineConfig, TestTradeManagerImpl tradeManager, BollingerBasedSecondStrategy secondStrategy, BollingerBasedOlderTimeFrameStrategy olderTimeFrameStrategy, INotifier notifier, IFakeOrderDao fakeOrderDao, BinanceMarketServiceImpl binanceMarketService) {
        this.stockMarketService = stockMarketService;
        this.klineConfig = klineConfig;
        this.tradeManager = tradeManager;
        this.secondStrategy = secondStrategy;
        this.olderTimeFrameStrategy = olderTimeFrameStrategy;
        this.notifier = notifier;
        this.fakeOrderDao = fakeOrderDao;
        this.binanceMarketService = binanceMarketService;
    }


    @Override
    @Scheduled(fixedDelay = 40_000, initialDelay = 1_000)
    @Async
    public void lookForEnterPosition() {

        KlineQueryDto queryDto = new KlineQueryDto();
        String timeFrame = klineConfig.getTimeFrame();
        queryDto.setLimit(klineConfig.getLimit());


        stockMarketService.getSpotSymbols().get().stream()
                .forEach(symbol -> {
                    queryDto.setSymbol(symbol);
                    queryDto.setInterval(timeFrame);
                    BarSeries series = stockMarketService.getBarSeries(queryDto).orElse(null);
                    Strategy strategy = secondStrategy.get(series);
                    if (strategy.shouldEnter(series.getEndIndex())) {
                        queryDto.setInterval("1h");
                        BarSeries secondSeries = stockMarketService.getBarSeries(queryDto).orElse(null);
                        Strategy olderStrategy = olderTimeFrameStrategy.get(secondSeries);
                        if (olderStrategy.shouldEnter(secondSeries.getEndIndex())) {
                            buy(symbol);
                            Event event = new Event();
                            event.setEventType(EEventType.BUYING);
                            event.setText(symbol);
                            notifier.notify(event);
                        }
                    }
                });
    }

    @Override
    public void lookForExitPosition() {
        if (secondStrategy.get().getExitRule() == null) return;

        tradeManager.sell(new OrderDto(), secondStrategy.get().getExitRule());

    }

    private void buy(String symbol) {
        Map<String, Object> map = new HashMap<>();
        map.put("symbol", symbol);
        SymbolPriceTickerDto tickerDto = binanceMarketService.getSymbolPriceTicker(map).get();

        FakeOrderEntity orderEntity = new FakeOrderEntity();
        orderEntity.setSymbol(symbol);
        orderEntity.setStatus(EOrderStatus.EXPIRED);
        orderEntity.setBuyTime(LocalDateTime.now());
        orderEntity.setBuyPrice(tickerDto.getPrice());

        fakeOrderDao.save(orderEntity);

    }

    @Scheduled(fixedDelay = 20_000, initialDelay = 1_000)
    @Async
    public void checkOrder() {

        fakeOrderDao
                .findAllByStatus(EOrderStatus.EXPIRED).ifPresent(
                        list -> list.forEach(fakeOrderEntity -> {
                            Map<String, Object> map = new HashMap<>();
                            BigDecimal price = fakeOrderEntity.getBuyPrice();
                            String symbol = fakeOrderEntity.getSymbol();

                            map.put("symbol", symbol);
                            SymbolPriceTickerDto tickerDto = binanceMarketService.getSymbolPriceTicker(map).get();
                            BigDecimal currentPrice = tickerDto.getPrice();

                            BigDecimal difference = currentPrice.subtract(price);

                            BigDecimal resultPercent = difference.multiply(BigDecimal.valueOf(100))
                                    .setScale(2, RoundingMode.HALF_UP)
                                    .divide(currentPrice, 2, RoundingMode.HALF_UP);

                            if(resultPercent.doubleValue() >= 0.8){
                                fakeOrderEntity.setStatus(EOrderStatus.FILLED);
                                fakeOrderEntity.setSellPrice(currentPrice);
                                fakeOrderEntity.setSellTime(LocalDateTime.now());
                                fakeOrderEntity.setResult(true);
                                fakeOrderEntity.setDuration(ChronoUnit.MINUTES.between(fakeOrderEntity.getBuyTime(), fakeOrderEntity.getSellTime()));
                                fakeOrderDao.save(fakeOrderEntity);

                                Event event = new Event();
                                event.setEventType(EEventType.SELLING);
                                event.setText("crypto active was sell");
                                notifier.notify(event);
                            }


                        })
                );
    }

}
