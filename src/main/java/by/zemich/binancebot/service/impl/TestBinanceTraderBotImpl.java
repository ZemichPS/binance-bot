package by.zemich.binancebot.service.impl;


import by.zemich.binancebot.DAO.api.IFakeOrderDao;
import by.zemich.binancebot.DAO.entity.FakeOrderEntity;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.service.api.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Component
@EnableScheduling
@Log4j2
public class TestBinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final INotifier notifier;

    private final IFakeOrderDao fakeOrderDao;
    private final Map<String, IStrategy> strategyMap = new HashMap<>();

    private final BigDecimal goal = new BigDecimal("0.7");

    public TestBinanceTraderBotImpl(IStockMarketService stockMarketService, INotifier notifier, IFakeOrderDao fakeOrderDao) {
        this.stockMarketService = stockMarketService;
        this.notifier = notifier;
        this.fakeOrderDao = fakeOrderDao;
    }


    @Override
    public void registerStrategy(String name, IStrategy strategyManager) {
        strategyMap.put(name, strategyManager);
    }

    @Override
    public void checkBargain() {

    }


    @Scheduled(fixedDelay = 40_000, initialDelay = 1_000)
    @Async
    @Override
    public void lookForEnterPosition() {

        KlineQueryDto queryDto = new KlineQueryDto();
        queryDto.setLimit(200);


        stockMarketService.getSpotSymbols().get()
                .forEach(symbol -> {

                    queryDto.setSymbol(symbol);
                    queryDto.setInterval("15m");
                    BarSeries series = stockMarketService.getBarSeries(queryDto).orElse(null);
                    Strategy strategy = strategyMap.get("BOLLINGER_BAND_MAIN_STRATEGY").get(series);
                    if (strategy.shouldEnter(series.getEndIndex())) {
                        queryDto.setInterval("1h");
                        BarSeries secondSeries = stockMarketService.getBarSeries(queryDto).orElse(null);
                        Strategy sureStrategy = strategyMap.get("BOLLINGER_BAND_OLDER_TIMEFRAME_STRATEGY").get(secondSeries);
                        if (sureStrategy.shouldEnter(secondSeries.getEndIndex())) {

                            FakeOrderEntity createdFakeOrder = createFakeOrder(symbol);
                            notifyToTelegram(createdFakeOrder, EEventType.ENDPOINT_WAS_FOUNDED);
                        }
                    }
                });
    }

    @Scheduled(fixedDelay = 15_000, initialDelay = 2_000)
    @Async
    public void checkOrder() {
        fakeOrderDao.findAllByStatus(EOrderStatus.NEW).ifPresent(list -> {
            list.forEach(this::check);
        });
    }

    private FakeOrderEntity createFakeOrder(String symbol) {
        BigDecimal price = getSymbolPrice(symbol);
        FakeOrderEntity fakeOrderEntity = new FakeOrderEntity();
        fakeOrderEntity.setBuyTime(LocalDateTime.now());
        fakeOrderEntity.setSymbol(symbol);
        fakeOrderEntity.setStatus(EOrderStatus.NEW);
        fakeOrderEntity.setBuyPrice(price);
        return fakeOrderDao.save(fakeOrderEntity);
    }

    private FakeOrderEntity check(FakeOrderEntity fakeOrderEntity) {
        BigDecimal currentPrice = getSymbolPrice(fakeOrderEntity.getSymbol());

        BigDecimal buyPrice = fakeOrderEntity.getBuyPrice();
        BigDecimal percentDifference = getPercentDifference(buyPrice, currentPrice);

        fakeOrderEntity.setCurrentResult(buyPrice.subtract(currentPrice));
        fakeOrderEntity.setDuration(Duration.between(fakeOrderEntity.getBuyTime(),
                LocalDateTime.now()).toMinutes());


        if (percentDifference.doubleValue() >= goal.doubleValue()) {
            fakeOrderEntity.setSellPrice(currentPrice);
            fakeOrderEntity.setSellTime(LocalDateTime.now());
            fakeOrderEntity.setStatus(EOrderStatus.STOPPED);

            notifyToTelegram(fakeOrderEntity, EEventType.ASSET_WAS_SOLD);
        }


        return fakeOrderDao.save(fakeOrderEntity);
    }

    private BigDecimal getSymbolPrice(String symbol) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", symbol);
        SymbolPriceTickerDto symbolPriceTicker = stockMarketService.getSymbolPriceTicker(params).orElseThrow();

        return symbolPriceTicker.getPrice();

    }

    private void notifyToTelegram(FakeOrderEntity fakeOrderEntity, EEventType eventType){
        EventDto event = EventDto
                .builder()
                .eventType(eventType)
                .text("Symbol: " + fakeOrderEntity.getSymbol())
                .build();

        notifier.notify(event);

    }


    private BigDecimal getPercentDifference(BigDecimal buyPrice, BigDecimal sellPrice) {

        BigDecimal difference = sellPrice.subtract(buyPrice);

        BigDecimal resultPercent = difference.multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                .divide(buyPrice, 2, RoundingMode.HALF_UP);

        return resultPercent;
    }

}








