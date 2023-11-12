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


import java.util.*;


@Component
@EnableScheduling
@Async
@Log4j2
public class BinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final ITradeManager tradeManager;
    private final INotifier notifier;
    private final IBargainService bargainService;
    private final ConversionService conversionService;
    private final Map<String, IStrategy> strategyMap = new HashMap<>();

    public BinanceTraderBotImpl(IStockMarketService stockMarketService,
                                ITradeManager tradeManager, INotifier notifier,
                                IBargainService bargainService,
                                ConversionService conversionService) {
        this.stockMarketService = stockMarketService;
        this.tradeManager = tradeManager;
        this.notifier = notifier;
        this.bargainService = bargainService;
        this.conversionService = conversionService;
    }

    @Override
    public void registerStrategy(String name, IStrategy strategyManager) {
        strategyMap.put(name, strategyManager);
    }


    @Scheduled(fixedDelay = 40_000, initialDelay = 1_000)
    @Async
    @Override
    public void lookForEnterPosition() {

        KlineQueryDto queryDto = new KlineQueryDto();
        queryDto.setLimit(500);


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
                            try {
                                OrderDto buyOrder = tradeManager.createBuyLimitOrderByBidPrice(symbol);
                                BargainDto newBargain = new BargainDto();
                                newBargain.setUuid(UUID.randomUUID());
                                newBargain.setStatus(EBargainStatus.OPEN);

                                newBargain.setOrders(List.of(buyOrder));
                                BargainEntity newBargainEntity = bargainService.create(newBargain).get();

                                EventDto event = EventDto.builder()
                                        .text(newBargainEntity.toString())
                                        .eventType(EEventType.BUY_LIMIT_ORDER)
                                        .build();

                                notifier.notify(event);
                            } catch (BinanceClientException binanceClientException) {
                                System.out.println(
                                        binanceClientException.getErrMsg()
                                );
                            }
                        }

                    }

                });
    }

    @Scheduled(fixedDelay = 20_000, initialDelay = 1_000)
    @Async
    @Override
    public void checkBargain() {

        bargainService.updateOpenStatus().get().stream()
                .map(bargainEntity -> conversionService.convert(bargainEntity, BargainDto.class))
                .forEach(bargainDto -> {
                    bargainDto.getOrders().stream()
                            .findFirst()
                            .filter(orderDto -> orderDto.getSide().equals(ESide.BUY))
                            .ifPresent(orderDto -> {
                                tradeManager.createSellLimitOrder(orderDto.getOrderId());
                                bargainDto.setStatus(EBargainStatus.OPEN_BUY_ORDER_FILLED);
                                bargainService.update(bargainDto);
                            });
                });

        bargainService.checkOnFinish().ifPresent(bargainEntities -> {
            bargainEntities.forEach(bargainEntity -> {
                BargainDto bargainDto = conversionService.convert(bargainEntity, BargainDto.class);
                bargainService.end(bargainDto);
            });
        });

        bargainService.checkOnExpired().ifPresent(bargainEntities -> {
            bargainEntities.forEach(bargainEntity -> {
                BargainDto bargainDto = conversionService.convert(bargainEntity, BargainDto.class);
                bargainService.endByReasonExpired(bargainDto);
            });

        });
    }


}








