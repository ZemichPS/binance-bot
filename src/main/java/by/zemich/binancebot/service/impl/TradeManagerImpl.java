package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.config.properties.RealTradeProperties;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.core.enums.*;
import by.zemich.binancebot.service.api.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


@Component
@Slf4j
public class TradeManagerImpl implements ITradeManager {

    private final IStockMarketService stockMarketService;
    private final IOrderStorageService orderService;
    private final RealTradeProperties tradeProperties;
    private final ConversionService conversionService;
    private final IAssetService assetService;
    private final IBargainStorageService bargainService;

    public TradeManagerImpl(IStockMarketService stockMarketService,
                            IOrderStorageService orderService,
                            RealTradeProperties tradeProperties,
                            ConversionService conversionService,
                            IAssetService assetService, IBargainStorageService bargainService) {
        this.stockMarketService = stockMarketService;
        this.orderService = orderService;
        this.tradeProperties = tradeProperties;
        this.conversionService = conversionService;
        this.assetService = assetService;
        this.bargainService = bargainService;
    }


    @Override
    public OrderDto createBuyLimitOrderByAskPrice(Asset symbol) {
        BigDecimal askPrice = stockMarketService.getAskPriceForAsset(symbol.getSymbol());
        BigDecimal quantity = tradeProperties.getDeposit().divide(askPrice, 1, RoundingMode.HALF_DOWN);

        RequestForNewOrderDto newOrderRequest = RequestForNewOrderDto.builder()
                .symbol(symbol.getSymbol())
                .price(askPrice)
                .side(ESide.BUY)
                .type(EOrderType.LIMIT)
                .quantity(quantity)
                .timeInForce(ETimeInForce.IOC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderDto newCreatedOrder = stockMarketService.createOrder(newOrderRequest);
        saveAndConvertOrderToDto(newCreatedOrder);

        return newCreatedOrder;
    }

    @Override
    public OrderDto createBuyLimitOrderByCurrentPrice(Asset assetForBuying) {
        PriceFilter priceFilter = assetForBuying.getPriceFilter();
        LotSizeFilter lotSizeBinanceFilter = assetForBuying.getLotSizeFilter();

        BigDecimal stepSize = lotSizeBinanceFilter.getStepSize();

        BigDecimal currentPrice = stockMarketService.getCurrentPriceForAsset(assetForBuying.getSymbol());
        BigDecimal quantityForBuying = tradeProperties.getDeposit().divide(currentPrice, 10, RoundingMode.DOWN);
        BigDecimal computedQuantity = getAssetQuantityUsingStepSize(quantityForBuying, stepSize).setScale(priceFilter.getTickSize().scale(), RoundingMode.HALF_UP);

        RequestForNewOrderDto requestForNewOrderDto = RequestForNewOrderDto.builder()
                .symbol(assetForBuying.getSymbol())
                .price(currentPrice)
                .side(ESide.BUY)
                .type(EOrderType.LIMIT)
                .quantity(computedQuantity)
                .timeInForce(ETimeInForce.GTC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderDto newCreatedOrder = stockMarketService.createOrder(requestForNewOrderDto);


        return saveAndConvertOrderToDto(newCreatedOrder);
    }

    @Override
    public OrderDto createSellLimitOrder(UUID buyOrderUuid, BigDecimal percentageAim) {

        OrderDto filledBuyOrder = getOrderByUuid(buyOrderUuid);
        String assetSymbol = filledBuyOrder.getSymbol();

        Asset assetForTrading = assetService.getBySymbol(assetSymbol);

        PriceFilter priceFilter = assetForTrading.getPriceFilter();
        LotSizeFilter lotSizeFilter = assetForTrading.getLotSizeFilter();
        BigDecimal takerFee = tradeProperties.getTaker();
        BigDecimal interest = getValueFromPercentage(filledBuyOrder.getPrice(), percentageAim);

        BigDecimal currentQuantity = filledBuyOrder.getOrigQty();
        BigDecimal stepSize = lotSizeFilter.getStepSize();
        BigDecimal sellQuantity = currentQuantity.subtract(getValueFromPercentage(currentQuantity, takerFee));
        BigDecimal computedQuantity = getAssetQuantityUsingStepSize(sellQuantity, stepSize).setScale(priceFilter.getTickSize().scale(), RoundingMode.UNNECESSARY);
        BigDecimal sellPrice = filledBuyOrder.getPrice().add(interest);

        BigDecimal computingSellPrice = getAssetPriceUsingStepSize(sellPrice, priceFilter.getTickSize()).setScale(priceFilter.getTickSize().scale(), RoundingMode.UNNECESSARY);


        RequestForNewOrderDto requestForNewSellOrder = RequestForNewOrderDto.builder()
                .symbol(assetSymbol)
                .price(computingSellPrice)
                .side(ESide.SELL)
                .type(EOrderType.LIMIT)
                .quantity(currentQuantity)
                .timeInForce(ETimeInForce.GTC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderDto newCreatedOrder = stockMarketService.createOrder(requestForNewSellOrder);
        return saveAndConvertOrderToDto(newCreatedOrder);
    }

    @Override
    public OrderDto createSellOrderByAscPrice(OrderDto orderDtoToSell) {

        String assetSymbol = orderDtoToSell.getSymbol();
        BigDecimal ascPrice = stockMarketService.getAskPriceForAsset(assetSymbol);
        BigDecimal currentQuantity = orderDtoToSell.getOrigQty();

        RequestForNewOrderDto requestForNewSellOrder = RequestForNewOrderDto.builder()
                .symbol(assetSymbol)
                .price(ascPrice)
                .side(ESide.SELL)
                .type(EOrderType.LIMIT)
                .quantity(currentQuantity)
                .timeInForce(ETimeInForce.GTC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderDto newCreatedOrder = stockMarketService.createOrder(requestForNewSellOrder);
        return saveAndConvertOrderToDto(newCreatedOrder);
    }

    @Override
    public OrderDto createSellOrderByMarketPrice(OrderDto orderDtoToSell) {
        String assetSymbol = orderDtoToSell.getSymbol();
        BigDecimal currentQuantity = orderDtoToSell.getOrigQty();

        RequestForNewOrderDto requestForNewSellOrder = RequestForNewOrderDto.builder()
                .symbol(assetSymbol)
                .side(ESide.SELL)
                .type(EOrderType.MARKET)
                .quantity(currentQuantity)
                .timeInForce(ETimeInForce.GTC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderDto newCreatedOrder = stockMarketService.createOrder(requestForNewSellOrder);
        return saveAndConvertOrderToDto(newCreatedOrder);
    }

    @Override
    public OrderDto createStopLimitOrder(Long orderId) {

        OrderDto buyOrderDto = getOrderById(orderId);
        String symbol = buyOrderDto.getSymbol();

        BigDecimal gain = getValueFromPercentage(buyOrderDto.getPrice(), tradeProperties.getGain());

        RequestForNewOrderDto requestForCreateStopLimitOrder = RequestForNewOrderDto.builder()
                .symbol(symbol)
                .price(buyOrderDto.getPrice().add(gain))
                .stopPrice(buyOrderDto.getPrice().add(gain))
                .side(ESide.SELL)
                .type(EOrderType.TAKE_PROFIT_LIMIT)
                .quantity(buyOrderDto.getExecutedQty())
                .timeInForce(ETimeInForce.IOC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderDto newCreatedOrder = stockMarketService.createOrder(requestForCreateStopLimitOrder);

        return saveAndConvertOrderToDto(newCreatedOrder);
    }

    @Override
    public OrderDto cancelOrder(OrderDto troubleOrder) {
        CancelOrderRequestDto cancelOrderRequestDto = CancelOrderRequestDto.builder()
                .symbol(troubleOrder.getSymbol())
                .orderId(troubleOrder.getOrderId())
                .build();

        CancelOrderResponseDto cancelOrderResponseDto = stockMarketService.cancelOrder(cancelOrderRequestDto);
        OrderDto canceledOrder = getOrderById(cancelOrderResponseDto.getOrderId());
        canceledOrder.setStatus(EOrderStatus.CANCELED);

        OrderDto canceledAndSavedOrderDto = saveAndConvertOrderToDto(canceledOrder);

        return canceledAndSavedOrderDto;
    }

    @Override
    public BargainDto completeBargainInTheRed(BargainDto bargainToCancel) {
        OrderDto canceledOrder = cancelOrder(bargainToCancel.getSellOrder());
        OrderDto createdSellOrder = createSellOrderByAscPrice(canceledOrder);
        bargainToCancel.setSellOrder(createdSellOrder);
        bargainToCancel.setStatus(EBargainStatus.CANCELED_IN_THE_RED);
        BargainEntity updatedBargain = bargainService.update(bargainToCancel).orElseThrow();
        return conversionService.convert(updatedBargain, BargainDto.class);
    }

    @Override
    public BargainDto completeBargainByReasonTimeoutBuyOrder(BargainDto troubleBargain) {

        OrderDto canceledOrder = cancelOrder(troubleBargain.getBuyOrder());
        troubleBargain.setBuyOrder(canceledOrder);
        troubleBargain.setStatus(EBargainStatus.CANCELED);
        BargainEntity completedBargain = bargainService.update(troubleBargain).orElseThrow();
        return conversionService.convert(completedBargain, BargainDto.class);
    }

    private OrderDto getOrderById(Long orderId) {
        OrderEntity orderEntity = orderService.getByOrderId(orderId).orElseThrow(RuntimeException::new);
        return conversionService.convert(orderEntity, OrderDto.class);
    }

    private OrderDto getOrderByUuid(UUID orderUuid) {
        OrderEntity orderEntity = orderService.getByUuid(orderUuid).orElseThrow(RuntimeException::new);
        return conversionService.convert(orderEntity, OrderDto.class);
    }

    private BigDecimal getValueFromPercentage(BigDecimal value, BigDecimal percent) {
        return value.multiply(percent).divide(new BigDecimal(100), RoundingMode.DOWN);
    }

    private BigDecimal getAssetQuantityUsingStepSize(BigDecimal quantity, BigDecimal stepSize) {
        BigDecimal rest = quantity.remainder(stepSize);
        return quantity.subtract(rest);
    }

    private BigDecimal getAssetPriceUsingStepSize(BigDecimal price, BigDecimal tickSize) {
        BigDecimal rest = price.remainder(tickSize);
        return price.subtract(rest);
    }

    private OrderDto saveAndConvertOrderToDto(OrderDto newOrder) {
        OrderEntity savedNewOrderEntity = orderService.save(newOrder).orElseThrow(RuntimeException::new);
        return conversionService.convert(savedNewOrderEntity, OrderDto.class);
    }


}
