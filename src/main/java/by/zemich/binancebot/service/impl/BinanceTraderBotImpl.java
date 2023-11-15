package by.zemich.binancebot.service.impl;


import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.EventDto;
import by.zemich.binancebot.core.dto.KlineQueryDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EBargainStatus;
import by.zemich.binancebot.core.enums.EEventType;
import by.zemich.binancebot.core.enums.ESide;
import by.zemich.binancebot.service.api.*;
import com.binance.connector.client.exceptions.BinanceClientException;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;


import java.text.MessageFormat;
import java.util.*;


@Component
@EnableScheduling
@Log4j2
public class BinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final ITradeManager tradeManager;
    private final INotifier notifier;
    private final IEventManager eventManager;
    private final IBargainService bargainService;
    private final ConversionService conversionService;
    private final Map<String, IStrategy> strategyMap = new HashMap<>();

    public BinanceTraderBotImpl(IStockMarketService stockMarketService,
                                ITradeManager tradeManager, INotifier notifier,
                                IEventManager eventManager, IBargainService bargainService,
                                ConversionService conversionService) {
        this.stockMarketService = stockMarketService;
        this.tradeManager = tradeManager;
        this.notifier = notifier;
        this.eventManager = eventManager;
        this.bargainService = bargainService;
        this.conversionService = conversionService;
    }

    @Override
    public void registerStrategy(String name, IStrategy strategyManager) {
        strategyMap.put(name, strategyManager);
    }


    @Scheduled(fixedDelay = 20_000, initialDelay = 1_000)
    @Async
    @Override
    public void checkBargain() {


        //проверка на исполнение ордера на покупку
        bargainService.updateOpenStatus().get().stream()
                .map(bargainEntity -> conversionService.convert(bargainEntity, BargainDto.class))
                .forEach(bargainDto -> {
                    bargainDto.getOrders().stream()
                            .filter(orderDto -> orderDto.getSide().equals(ESide.BUY))
                            .findFirst()
                            .ifPresent(orderDto -> {
                                tradeManager.createSellLimitOrder(orderDto.getOrderId());
                                bargainDto.setStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED);
                                bargainService.update(bargainDto);
                            });
                });


        // установка временных результатов
        bargainService.setTemporaryResult();

        //проверка на окончание сделки
        bargainService.checkOnFinish().ifPresent(bargainEntities -> {
            bargainEntities.forEach(bargainEntity -> {
                BargainDto bargainDto = conversionService.convert(bargainEntity, BargainDto.class);
                bargainService.end(bargainDto);
            });
        });


        //проверка на истёкший ордер
        bargainService.checkOnExpired().ifPresent(bargainEntities -> {
            bargainEntities.forEach(bargainEntity -> {
                BargainDto bargainDto = conversionService.convert(bargainEntity, BargainDto.class);
                bargainService.endByReasonExpired(bargainDto);
            });

        });
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
                     //   if (sureStrategy.shouldEnter(secondSeries.getEndIndex())) {
                        if (true) {
                            //createBargain(symbol);
                            creatFakeBargain(symbol);
                        }
                    }
                });
    }

    private void createBargain(String symbol) {
        try {
            OrderDto buyOrder = tradeManager.createBuyLimitOrderByBidPrice(symbol);
            BargainDto newBargain = new BargainDto();
            newBargain.setUuid(UUID.randomUUID());
            newBargain.setStatus(EBargainStatus.OPEN_BUY_ORDER_CREATED);
            newBargain.setOrders(List.of(buyOrder));
            newBargain.setSymbol(symbol);
            BargainEntity newBargainEntity = bargainService.create(newBargain).orElseThrow();
            BargainDto createdBargain = conversionService.convert(newBargainEntity, BargainDto.class);
            EventDto event = eventManager.get(EEventType.BARGAIN_WAS_CREATED, createdBargain);
            notifier.notify(event);

        } catch (BinanceClientException binanceClientException) {
            log.error(binanceClientException.getErrMsg(), binanceClientException.getCause().getMessage());
            EventDto event = eventManager.get(EEventType.ERROR, binanceClientException);

            notifier.notify(event);
        }
    }

    private void creatFakeBargain(String symbol) {
        notifier.notify(EventDto.builder()
                .eventType(EEventType.ENDPOINT_WAS_FOUNDED)
                .text(MessageFormat.format("Symbol: {0}", symbol))
                .build());
    }


}








