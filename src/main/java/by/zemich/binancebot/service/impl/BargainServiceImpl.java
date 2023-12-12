package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.api.IBargainDao;
import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.config.properties.RealTradeProperties;
import by.zemich.binancebot.core.dto.BargainCreateDto;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EBargainStatus;
import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.core.exeption.BadOrderStatusException;
import by.zemich.binancebot.service.api.IBargainService;
import by.zemich.binancebot.service.api.IOrderService;
import by.zemich.binancebot.service.api.IStockMarketService;
import by.zemich.binancebot.service.api.ITradeManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Log4j2
public class BargainServiceImpl implements IBargainService {


    private final IBargainDao bargainDao;
    private final ConversionService conversionService;
    private final IOrderService orderService;
    private final IStockMarketService stockMarketService;
    private final RealTradeProperties tradeProperties;
    private final ITradeManager tradeManager;


    public BargainServiceImpl(IBargainDao bargainDao, ConversionService conversionService, IOrderService orderService, IStockMarketService stockMarketService, RealTradeProperties tradeProperties, ITradeManager tradeManager) {
        this.bargainDao = bargainDao;
        this.conversionService = conversionService;
        this.orderService = orderService;
        this.stockMarketService = stockMarketService;
        this.tradeProperties = tradeProperties;
        this.tradeManager = tradeManager;
    }

    @Override
    @Transactional
    public Optional<BargainEntity> save(BargainDto bargainDto) {

        BargainEntity newBargainEntity = conversionService.convert(bargainDto, BargainEntity.class);
        BargainEntity savedBargainEntity = bargainDao.save(newBargainEntity);
        return Optional.of(savedBargainEntity);
    }

    @Override
    public BargainDto addBuyOrder(BargainDto bargainDto, OrderDto buyOrder) {
        OrderDto verifiedBuyOrder = verifyOrder(buyOrder);
        bargainDto.setBuyOrder(verifiedBuyOrder);
        return bargainDto;
    }

    @Override
    public boolean existsByStatusAndSymbol(EBargainStatus status, String symbol) {
        return bargainDao.existsByStatusAndSymbol(status, symbol);
    }

    @Override
    public boolean existsBySymbolAndStatusNotLike(String symbol, EBargainStatus status) {
        return bargainDao.existsBySymbolAndStatusNotLike(symbol, status);
    }

    @Override
    public BargainDto addSellOrder(BargainDto bargainDto, OrderDto sellOrder) {
        OrderDto verifiedSellOrder = verifyOrder(sellOrder);
        bargainDto.setSellOrder(verifiedSellOrder);
        return bargainDto;
    }

    @Override
    public BargainEntity cancelBuyOrderAndSetCancelStatusAndSave(BargainDto troubleBargain) {
        tradeManager.cancelOrder(troubleBargain.getBuyOrder().getUuid());
        troubleBargain.setStatus(EBargainStatus.CANCELED);
        BargainEntity bargainToCancel = conversionService.convert(troubleBargain, BargainEntity.class);
        return bargainDao.save(bargainToCancel);
    }

    @Override
    public BargainDto create(BargainCreateDto bargainCreateDto) {

        BargainDto newBargain = new BargainDto();
        newBargain.setUuid(UUID.randomUUID());
        newBargain.setStatus(EBargainStatus.CREATED);
        newBargain.setSymbol(bargainCreateDto.getSymbol().getSymbol());
        newBargain.setStrategy(bargainCreateDto.getStrategy());
        return newBargain;

}

    @Override
    @Transactional
    public Optional<BargainEntity> update(BargainDto bargainDtoForUpdate) {
        BargainEntity bargainEntity = conversionService.convert(bargainDtoForUpdate, BargainEntity.class);
        BargainEntity savedBargainEntity = bargainDao.save(bargainEntity);
        return Optional.of(savedBargainEntity);
    }

    @Override
    public Optional<BargainEntity> endByReasonExpired(BargainDto bargainDto) {

        bargainDto.setStatus(EBargainStatus.BUY_ORDER_WAS_EXPIRED);

        BargainEntity bargainEntity = conversionService.convert(bargainDto, BargainEntity.class);
        BargainEntity savedEntity = bargainDao.save(bargainEntity);

        return Optional.of(savedEntity);

    }

    @Override
    public BargainEntity finalize(BargainDto bargainDto) {

        // тут всякие расчёты и просчёты

        OrderDto buyOrder = bargainDto.getBuyOrder();
        OrderDto sellOrder = bargainDto.getSellOrder();

        BigDecimal soldAssetQuantity = sellOrder.getOrigQty();

        Timestamp currenTime = Timestamp.from(Instant.now());

        Duration timeInWork = getDurationBetweenStartBargainAndNow(bargainDto.getDtCreate()); ;

        BigDecimal buyPrice = buyOrder.getPrice();
        BigDecimal sellPrice = sellOrder.getPrice();

        BigDecimal percentageResult = getPercentDifference(buyPrice, sellPrice);
        BigDecimal financeResult = sellPrice.subtract(buyPrice).multiply(soldAssetQuantity);

        bargainDto.setFinishTime(currenTime);
        bargainDto.setTimeInWork(timeInWork.toMinutes());
        bargainDto.setPercentageResult(percentageResult);
        bargainDto.setFinanceResult(financeResult);
        bargainDto.setStatus(EBargainStatus.FINISHED);

        BargainEntity bargainEntity = conversionService.convert(bargainDto, BargainEntity.class);
        return bargainDao.save(bargainEntity);


    }



    @Override
    @Transactional(readOnly = true)
    public Optional<List<BargainEntity>> getAll() {
        List<BargainEntity> bargainEntities = bargainDao.findAll();
        return Optional.of(bargainEntities);
    }

    @Override
    public Optional<List<BargainEntity>> getAllWithFilledBuyOrders() {
        List<BargainEntity> bargainEntities = new ArrayList<>();

        bargainDao.findAllByStatus(EBargainStatus.OPEN_BUY_ORDER_CREATED).ifPresent(
                bargainEntityList -> bargainEntityList.forEach(bargainEntity -> {

                            if (Objects.nonNull(bargainEntity.getBuyOrder())) {
                                OrderDto orderDto = conversionService.convert(bargainEntity.getBuyOrder(), OrderDto.class);
                                // сравниваем статусы
                                if (orderService.updateStatus(orderDto, EOrderStatus.FILLED).isPresent()) {
                                    bargainEntities.add(bargainEntity);
                                }
                            }
                        }
                )
        );
        return Optional.of(bargainEntities);
    }

    @Override
    public Optional<List<BargainEntity>> getAllWithExpiredBuyOrders() {
        List<BargainEntity> bargainEntities = new ArrayList<>();

        bargainDao.findAllByStatus(EBargainStatus.CREATED).orElseThrow().
                forEach(bargainEntity -> {
                            if (bargainEntity.getBuyOrder() != null && bargainEntity.getSellOrder() == null) {
                                OrderDto orderDto = conversionService.convert(bargainEntity.getBuyOrder(), OrderDto.class);
                                // сравниваем статусы
                                if (orderService.updateStatus(orderDto, EOrderStatus.EXPIRED).isPresent()) {
                                    bargainEntities.add(bargainEntity);
                                }
                            }
                        }
                );
        return Optional.of(bargainEntities);
    }

    @Override
    public Optional<List<BargainEntity>> checkOnFinish() {
        List<BargainEntity> bargainEntities = new ArrayList<>();

        bargainDao.findAllByStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED).orElseThrow().forEach(bargainEntity -> {

                    if (Objects.nonNull(bargainEntity.getBuyOrder()) && Objects.nonNull(bargainEntity.getSellOrder())) {
                        OrderEntity sellOrderEntity = bargainEntity.getSellOrder();

                        OrderDto sellOrderDto = conversionService.convert(sellOrderEntity, OrderDto.class);
                        // сравниваем статусы
                        if (orderService.updateStatus(sellOrderDto, EOrderStatus.FILLED).isPresent()) {
                            bargainEntities.add(bargainEntity);
                        }
                    }
                }
        );
        return Optional.of(bargainEntities);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<BargainEntity>> getAllByStatus(EBargainStatus status) {
        List<BargainEntity> bargainEntities = bargainDao.findAllByStatus(status).orElseThrow();
        return Optional.of(bargainEntities);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BargainEntity> getByUuid(UUID uuid) {
        return Optional.of(bargainDao.findById(uuid).get());
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        bargainDao.deleteById(uuid);
    }

    private BigDecimal getPercentDifference(BigDecimal buyPrice, BigDecimal sellPrice) {

        BigDecimal difference = sellPrice.subtract(buyPrice);

        return difference.multiply(BigDecimal.valueOf(100))
                .divide(buyPrice, 3, RoundingMode.HALF_UP);
    }

    final BigDecimal getCurrentPriceBySymbol(String symbol) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", symbol);
        return stockMarketService.getSymbolPriceTicker(params).orElseThrow().getPrice();
    }

    public BargainEntity updateResult(BargainDto bargainDto) {

        if (!Objects.nonNull(bargainDto.getBuyOrder())) throw new RuntimeException("Bargain doesn't contain buy order");

        OrderDto buyOrderDto = bargainDto.getBuyOrder();
        BigDecimal boughtAssetQuantity = buyOrderDto.getOrigQty();
        Duration timeInWork = getDurationBetweenStartBargainAndNow(bargainDto.getDtCreate());
        BigDecimal buyPrice = buyOrderDto.getPrice();
        BigDecimal currentPrice = getCurrentPriceBySymbol(bargainDto.getSymbol());

        BigDecimal financeResult = currentPrice.subtract(buyPrice).multiply(boughtAssetQuantity).setScale(3, RoundingMode.HALF_UP);
        BigDecimal percentageResult = getPercentDifference(buyPrice, currentPrice);

        bargainDto.setFinanceResult(financeResult);
        bargainDto.setCurrentPercentageResult(percentageResult);
        bargainDto.setTimeInWork(timeInWork.toMinutes());

        BargainEntity bargainEntity = conversionService.convert(bargainDto, BargainEntity.class);

        return bargainDao.save(bargainEntity);
    }

    private OrderDto verifyOrder(OrderDto orderDto){
        EOrderStatus status = orderDto.getStatus();
        if(status.equals(EOrderStatus.EXPIRED) || status.equals(EOrderStatus.REJECTED) || status.equals(EOrderStatus.CANCELED))
            throw new BadOrderStatusException( String.format("Bad order status: %s", status.name()));

        return orderDto;

    }

    private Duration getDurationBetweenStartBargainAndNow(Timestamp startBargain){
        return Duration.between(startBargain.toLocalDateTime(), LocalDateTime.now());
    }




}
