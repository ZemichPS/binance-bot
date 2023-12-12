package by.zemich.binancebot.service.impl;


import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.config.properties.RealTradeProperties;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.KlineQueryDto;
import by.zemich.binancebot.core.enums.*;
import by.zemich.binancebot.service.api.*;
import com.binance.connector.client.exceptions.BinanceClientException;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;


import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


@Service
@EnableScheduling
@Log4j2
public class BinanceTraderBotImpl implements ITraderBot {
    private final IStockMarketService stockMarketService;
    private final ITradeManager tradeManager;
    private final INotifier notifier;
    private final IEventManager eventManager;
    private final IBargainService bargainService;
    private final ConversionService conversionService;
    private final IIndicatorReader indicatorReader;
    private final IAssetService assetService;
    private final RealTradeProperties tradeProperties;
    private final Map<String, IStrategy> strategyMap = new HashMap<>();
    private final List<String> blackList = new ArrayList<>();

    // TODO удалить (счётчик разрешённых покупок актива)
    private Integer counter = 1244545;
    private boolean accountHasInsufficientBalance = true;


    public BinanceTraderBotImpl(IStockMarketService stockMarketService,
                                ITradeManager tradeManager,
                                INotifier notifier,
                                IEventManager eventManager,
                                IBargainService bargainService,
                                ConversionService conversionService,
                                IIndicatorReader indicatorReader,
                                IAssetService assetService, RealTradeProperties tradeProperties) {
        this.stockMarketService = stockMarketService;
        this.tradeManager = tradeManager;
        this.notifier = notifier;
        this.eventManager = eventManager;
        this.bargainService = bargainService;
        this.conversionService = conversionService;
        this.indicatorReader = indicatorReader;
        this.assetService = assetService;
        this.tradeProperties = tradeProperties;

        blackList.add("BNBUSDT");
        blackList.add("BUSDUSDT");
        blackList.add("TUSDUSDT");
        blackList.add("USDTUSDT");
        blackList.add("TUSDTUSDT");
        blackList.add("USDCUSDT");
        blackList.add("BTTCUSDT");
        blackList.add("PEPEUSDT");
    }


    @Override
    public void registerStrategy(String name, IStrategy strategyManager) {
        strategyMap.put(name, strategyManager);
    }

    @Scheduled(fixedDelay = 40_000, initialDelay = 10_000)
    @Override
    public void lookForEnterPosition() {

        KlineQueryDto klinequeryDto = new KlineQueryDto();
        klinequeryDto.setLimit(500);

        //TODO удалить для реального трейдинга (позволяет совершить ограниченное количество сделок)
        if (counter <= 0) return;
        if (!accountHasInsufficientBalance) return;

        strategyMap.values().stream()
                .map(IStrategy::getInterval)
                .sorted()
                .map(EInterval::toString)
                .distinct()
                .forEach(
                        stringInterval -> {
                            assetService.getListToUSDTForSpotTrading().forEach(
                                    symbol -> {

                                        if (blackList.contains(symbol.getSymbol().trim())) return;

                                        klinequeryDto.setSymbol(symbol.getSymbol());
                                        klinequeryDto.setInterval(stringInterval);

                                        BarSeries series = stockMarketService.getBarSeries(klinequeryDto).orElseThrow(RuntimeException::new);

                                        if (series.getBarCount() < 500) return;

                                        strategyMap.values().stream()
                                                .filter(strategy -> strategy.getInterval().toString().equals(stringInterval))
                                                .forEach(strategy -> {
                                                    if (strategy.getEnterRule(series).isSatisfied(series.getEndIndex())) {

                                                        //TODO удалить для реального трейдинга (позволяет совершить ограниченное количество сделок)
                                                        if (counter <= 0) return;

                                                        log.info(indicatorReader.getValues(series));

                                                        if (Objects.nonNull(strategy.getAdditionalStrategy())) {

                                                            IStrategy additionalStrategy = strategy.getAdditionalStrategy();
                                                            EInterval intervalForAdditionalStrategy = additionalStrategy.getInterval();
                                                            BarSeries additionalSeries = series;

                                                            if (!klinequeryDto.getInterval().equals(intervalForAdditionalStrategy.toString())) {
                                                                KlineQueryDto additionalKlineQuery = KlineQueryDto.builder()
                                                                        .interval(intervalForAdditionalStrategy.toString())
                                                                        .symbol(klinequeryDto.getSymbol())
                                                                        .limit(klinequeryDto.getLimit())
                                                                        .build();
                                                                additionalSeries = stockMarketService.getBarSeries(additionalKlineQuery).orElseThrow(RuntimeException::new);
                                                            }

                                                            Rule additionalRule = additionalStrategy.getEnterRule(additionalSeries);
                                                            if (!additionalRule.isSatisfied(additionalSeries.getEndIndex()))
                                                                return;
                                                        }

                                                        //         IndicatorValuesDto indicatorValues = indicatorReader.getValues(series);

                                                        BargainCreateDto bargainCreateDto = BargainCreateDto.builder()
                                                                .strategy(strategy.getName())
                                                                .symbol(symbol)
                                                                .percentageAim(strategy.getInterest())
                                                                .build();

                                                        BargainDto createdBargain = createBargain(bargainCreateDto);

                                                        if (Objects.nonNull(createdBargain)) {
                                                            EventDto eventDto = eventManager.get(EEventType.BARGAIN_WAS_CREATED, createdBargain);
                                                            notifier.notify(eventDto);
                                                        }

                                                    }
                                                });
                                    });
                        });
    }


    @Scheduled(fixedDelay = 30_000, initialDelay = 10_000)
    @Async
    @Override
    public void checkBargain() {

        //проверка на исполнение ордера на покупку
        bargainService.getAllWithFilledBuyOrders().ifPresent(
                entities -> {
                    entities.stream()
                            .map(bargainEntity -> conversionService.convert(bargainEntity, BargainDto.class))
                            .filter(Objects::nonNull)
                            .filter(bargainDto -> bargainDto.getBuyOrder() != null)
                            .forEach(bargainDto -> {

                                OrderDto buyOrderDto = bargainDto.getBuyOrder();

                                BigDecimal percentageAim = strategyMap.get(bargainDto.getStrategy()).getInterest();

                                OrderDto sellOrderDto = tradeManager.createSellLimitOrder(buyOrderDto.getUuid(), percentageAim);
                                bargainDto.setSellOrder(sellOrderDto);
                                bargainDto.setStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED);
                                BargainEntity savedBargainEntity = bargainService.update(bargainDto).orElseThrow(RuntimeException::new);
                                BargainDto savedBargainDto = conversionService.convert(savedBargainEntity, BargainDto.class);
                                EventDto eventDto = eventManager.get(EEventType.SELL_LIMIT_ORDER_WAS_PLACED, savedBargainDto);
                                notifier.notify(eventDto);
                            });
                });


        //проверка на исполнение ордера на продажу
        bargainService.checkOnFinish().orElseThrow()
                .stream()
                .map(bargainEntity -> conversionService.convert(bargainEntity, BargainDto.class))
                .forEach(bargainDto -> {

                    BargainEntity finalizedBargainEntity = bargainService.finalize(bargainDto);
                    BargainDto finalizedBargainDto = conversionService.convert(finalizedBargainEntity, BargainDto.class);
                    EventDto eventDto = eventManager.get(EEventType.ASSET_WAS_SOLD, finalizedBargainDto);
                    notifier.notify(eventDto);

                    if (!accountHasInsufficientBalance) accountHasInsufficientBalance = true;

                });


        // установка временных результатов
        bargainService.getAllByStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED).ifPresent(
                listOfBargainEntities -> listOfBargainEntities.stream()
                        .filter(Objects::nonNull)
                        .map(bargainEntity -> conversionService.convert(bargainEntity, BargainDto.class))
                        .forEach(bargainService::updateResult));

/*
        //проверка на слишком долгое время ожидание покупки актива
        bargainService.getAllByStatus(EBargainStatus.OPEN_BUY_ORDER_CREATED).orElseThrow()
                .stream().map(bargainEntity -> conversionService.convert(bargainEntity, BargainDto.class))
                .forEach(bargainDto -> {
                    LocalDateTime bargainCreateDateTime = bargainDto.getDtCreate().toLocalDateTime();

                    long duration = Duration.between(bargainCreateDateTime, LocalDateTime.now()).toMinutes();
                    long criticalTime = tradeProperties.getCriticalLostTimeForBuying();

                    if (duration > criticalTime) {
                        BargainEntity canceledBargainEntity = bargainService.cancelBuyOrderAndSetCancelStatusAndSave(bargainDto);
                        BargainDto canceledBargainDto = conversionService.convert(canceledBargainEntity, BargainDto.class);
                        EventDto eventDto = eventManager.get(EEventType.BARGAIN_WAS_CANCELED, canceledBargainDto);
                        notifier.notify(eventDto);
                    }

                });
                */

    }

    private BargainDto createBargain(BargainCreateDto bargainCreateDto) {
        try {

          if(bargainService.existsBySymbolAndStatusNotLike(bargainCreateDto.getSymbol().getSymbol(), EBargainStatus.FINISHED))
                throw new RuntimeException("Active bargain with such asset already exists.");

            //TODO удалить для реального трейдинга (позволяет совершить ограниченное количество сделок)
            counter = counter - 1;

            OrderDto buyOrder = tradeManager.createBuyLimitOrderByCurrentPrice(bargainCreateDto.getSymbol());

            BargainDto newBargain = bargainService.create(bargainCreateDto);
            bargainService.addBuyOrder(newBargain, buyOrder);
            newBargain.setStatus(EBargainStatus.OPEN_BUY_ORDER_CREATED);
            newBargain.setInterest(bargainCreateDto.getPercentageAim());

            if (buyOrder.getStatus().equals(EOrderStatus.FILLED)) {
                BigDecimal percentageAim = bargainCreateDto.getPercentageAim();
                OrderDto sellOrderDto = tradeManager.createSellLimitOrder(buyOrder.getUuid(), percentageAim);
                newBargain.setSellOrder(sellOrderDto);
                newBargain.setStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED);
            }

            BargainEntity newBargainEntity = bargainService.save(newBargain).orElseThrow();
            return conversionService.convert(newBargainEntity, BargainDto.class);

        } catch (BinanceClientException binanceClientException) {
            log.error(binanceClientException.getErrMsg(), binanceClientException.getMessage());
            EventDto event = eventManager.get(EEventType.ERROR, binanceClientException);
            notifier.notify(event);
            if (binanceClientException.getMessage().contains("Account has insufficient balance for requested action"))
                accountHasInsufficientBalance = false;

            throw new RuntimeException(binanceClientException.getCause());
        }

    }




}








