package by.zemich.binancebot.service.impl;


import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.EventDto;
import by.zemich.binancebot.core.dto.binance.KlineQueryDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.*;
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
@Log4j2
public class BinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final ITradeManager tradeManager;
    private final INotifier notifier;
    private final IEventManager eventManager;
    private final IBargainService bargainService;
    private final ConversionService conversionService;
    private final Map<String, IStrategy> strategyMap = new HashMap<>();
    private final List<String> blackList = new ArrayList<>();
    private Integer counter = Integer.valueOf(1);

    public BinanceTraderBotImpl(IStockMarketService stockMarketService,
                                ITradeManager tradeManager,
                                INotifier notifier,
                                IEventManager eventManager,
                                IBargainService bargainService,
                                ConversionService conversionService) {
        this.stockMarketService = stockMarketService;
        this.tradeManager = tradeManager;
        this.notifier = notifier;
        this.eventManager = eventManager;
        this.bargainService = bargainService;
        this.conversionService = conversionService;

        blackList.add("BUSDUSDT");
        blackList.add("TUSDUSDT");
        blackList.add("USDTUSDT");
        blackList.add("TUSDTUSDT");
        blackList.add("USDCUSDT");
        blackList.add("BTTCUSDT");
        blackList.add("PEPEUSDT");
        blackList.add("BNBUSDT");
        blackList.add("NEOUSDT");
    }


    @Override
    public void registerStrategy(String name, IStrategy strategyManager) {
        strategyMap.put(name, strategyManager);
    }

    @Scheduled(fixedDelay = 40_000, initialDelay = 1_000)
    @Override
    public void lookForEnterPosition() {

        if (counter <= 0) return;

        KlineQueryDto queryDto = new KlineQueryDto();
        queryDto.setLimit(500);


        stockMarketService.getSpotSymbols().orElseThrow()
                .forEach(symbol -> {

                    if (blackList.contains(symbol)) return;

                    queryDto.setSymbol(symbol);
                    queryDto.setInterval(EInterval.M30.toString());

                    BarSeries series = stockMarketService.getBarSeries(queryDto).orElseThrow(RuntimeException::new);

                    Strategy mainStrategy = strategyMap.get("BOLLINGER_BAND_MAIN_STRATEGY").get(series);
                    Strategy additionalStrategy = strategyMap.get("BEAR_CANDLESTICK_UNDER_BBM").get(series);

                    if (mainStrategy.shouldEnter(series.getEndIndex())) {

                        //      if (additionalStrategy.shouldEnter(series.getBarCount() - 2)) {

                            synchronized (BinanceTraderBotImpl.class) {
                                if (counter <= 0) return;
                                createBargain(symbol);



                        }
                    }
                });
    }


    @Scheduled(fixedDelay = 15_000, initialDelay = 1_000)
    @Async
    @Override
    public void checkBargain() {
        synchronized (BinanceTraderBotImpl.class) {
            //проверка на исполнение ордера на покупку
            bargainService.checkOnFillBuyOrder().ifPresent(
                    entities -> {
                        entities.stream()
                                .map(bargainEntity -> conversionService.convert(bargainEntity, BargainDto.class))
                                .forEach(bargainDto -> {
                                    bargainDto.getOrders().stream()
                                            .filter(orderDto -> orderDto.getSide().equals(ESide.BUY))
                                            .findFirst()
                                            .ifPresent(buyOrderDto -> {
                                                OrderDto sellOrderDto = tradeManager.createSellLimitOrder(buyOrderDto.getOrderId());
                                                bargainDto.getOrders().add(sellOrderDto);
                                                bargainDto.setStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED);
                                                bargainService.update(bargainDto);

                                                EventDto eventDto = EventDto.builder()
                                                        .eventType(EEventType.SELL_LIMIT_ORDER)
                                                        .text("sell limit order was placed. Symbol: " + sellOrderDto.getSymbol())
                                                        .build();

                                                notifier.notify(eventDto);

                                            });
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
    }

    private void createBargain(String symbol) {
        try {


            OrderDto buyOrder = tradeManager.createBuyLimitOrderByBidPrice(symbol);



            BargainDto newBargain = new BargainDto();
            newBargain.setUuid(UUID.randomUUID());
            newBargain.setStatus(EBargainStatus.OPEN_BUY_ORDER_CREATED);
            newBargain.setOrders(List.of(buyOrder));

            if(buyOrder.getStatus().equals(EOrderStatus.FILLED)){
                OrderDto sellOrderDto = tradeManager.createSellLimitOrder(buyOrder.getOrderId());
                newBargain.getOrders().add(sellOrderDto);
            }

            newBargain.setSymbol(symbol);

            BargainEntity newBargainEntity = bargainService.create(newBargain).orElseThrow();


            BargainDto createdBargain = conversionService.convert(newBargainEntity, BargainDto.class);

            EventDto event = eventManager.get(EEventType.BARGAIN_WAS_CREATED, createdBargain);
            notifier.notify(event);


            counter--;

        } catch (BinanceClientException binanceClientException) {
            log.error(binanceClientException.getErrMsg(), binanceClientException.getMessage());
            EventDto event = eventManager.get(EEventType.ERROR, binanceClientException);

            notifier.notify(event);
        }
    }

}








