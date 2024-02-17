package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.config.properties.RealTradeProperties;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.core.enums.*;
import by.zemich.binancebot.core.exeption.NoSuchEntityException;
import by.zemich.binancebot.service.api.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class OrderFacadeImpl implements OrderFacade {

    private final IOrderStockMarketService orderStockMarketService;
    private final AssetFacade assetFacade;
    private final OrderService orderStorageService;
    private final ConversionService conversionService;
    private final RealTradeProperties tradeProperties;

    public OrderFacadeImpl(IOrderStockMarketService orderStockMarketService, AssetFacade assetFacade, OrderService orderStorageService, ConversionService conversionService, RealTradeProperties tradeProperties) {
        this.orderStockMarketService = orderStockMarketService;
        this.assetFacade = assetFacade;
        this.orderStorageService = orderStorageService;
        this.conversionService = conversionService;
        this.tradeProperties = tradeProperties;
    }


    @Override
    public OrderDto createBuyLimitOrderByCurrentPrice(Asset assetForBuying, BigDecimal deposit) {

        PriceFilter priceFilter = assetForBuying.getPriceFilter();
        LotSizeFilter lotSizeBinanceFilter = assetForBuying.getLotSizeFilter();

        BigDecimal stepSize = lotSizeBinanceFilter.getStepSize();

        BigDecimal currentPrice = assetFacade.getCurrentPrice(assetForBuying.getSymbol());
        BigDecimal quantityForBuying = deposit.divide(currentPrice, 10, RoundingMode.DOWN);
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

        OrderDto newCreatedOrder = orderStockMarketService.createOrder(requestForNewOrderDto);
        OrderEntity savedOrderEntity = orderStorageService.save(newCreatedOrder).orElseThrow();

        return convertOrderEntityToOrderDto(savedOrderEntity);
    }

    @Override
    public OrderDto createSellLimitOrder(OrderDto buyOrder, BigDecimal percentageAim) {

        String assetSymbol = buyOrder.getSymbol();

        Asset assetForTrading = assetFacade.getBySymbol(assetSymbol);

        PriceFilter priceFilter = assetForTrading.getPriceFilter();
        LotSizeFilter lotSizeFilter = assetForTrading.getLotSizeFilter();
        BigDecimal takerFee = tradeProperties.getTaker();
        BigDecimal interest = getValueFromPercentage(buyOrder.getPrice(), percentageAim);

        BigDecimal currentQuantity = buyOrder.getOrigQty();
        BigDecimal stepSize = lotSizeFilter.getStepSize();
        BigDecimal sellQuantity = currentQuantity.subtract(getValueFromPercentage(currentQuantity, takerFee));

        // ПАРАМЕТР НУЖЕН ЕСЛИ НЕТ BMB ДЛЯ ОПЛАТЫ КОММИССИИ
        BigDecimal computedQuantity = getAssetQuantityUsingStepSize(sellQuantity, stepSize).setScale(priceFilter.getTickSize().scale(), RoundingMode.UNNECESSARY);

        BigDecimal sellPrice = buyOrder.getPrice().add(interest);

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

        OrderDto newCreatedOrder = orderStockMarketService.createOrder(requestForNewSellOrder);
        OrderEntity savedOrderEntity = orderStorageService.save(newCreatedOrder).orElseThrow();
        return convertOrderEntityToOrderDto(savedOrderEntity);

    }

    @Override
    public OrderDto createSellOrderByAscPrice(OrderDto orderDtoToSell) {
        String assetSymbol = orderDtoToSell.getSymbol();
        BigDecimal ascPrice = assetFacade.getAskPrice(assetSymbol);

        RequestForNewOrderDto requestForNewSellOrder = RequestForNewOrderDto.builder()
                .symbol(assetSymbol)
                .price(ascPrice)
                .side(ESide.SELL)
                .type(EOrderType.LIMIT)
                .quantity(orderDtoToSell.getOrigQty())
                .timeInForce(ETimeInForce.GTC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderDto newCreatedOrder = orderStockMarketService.createOrder(requestForNewSellOrder);
        OrderEntity savedOrderEntity = orderStorageService.save(newCreatedOrder).orElseThrow();
        return convertOrderEntityToOrderDto(savedOrderEntity);
    }

    @Override
    public OrderDto createSellOrderByMarketPrice(OrderDto orderDtoToSell) {

        RequestForNewOrderDto requestForNewSellOrder = RequestForNewOrderDto.builder()
                .symbol(orderDtoToSell.getSymbol())
                .side(ESide.SELL)
                .type(EOrderType.MARKET)
                .quantity(orderDtoToSell.getOrigQty())
                .timeInForce(ETimeInForce.GTC)
                .newOrderRespType(ENewOrderRespType.FULL)
                .build();

        OrderDto newCreatedOrder = orderStockMarketService.createOrder(requestForNewSellOrder);
        OrderEntity savedOrderEntity = orderStorageService.save(newCreatedOrder).orElseThrow();
        return convertOrderEntityToOrderDto(savedOrderEntity);
    }

    @Override
    public OrderDto createStopLimitOrder(OrderDto order) {
        return null;
    }

    @Override
    public OrderDto cancelOrder(OrderDto troubleOrder) {
        CancelOrderRequestDto cancelOrderRequestDto = CancelOrderRequestDto.builder()
                .symbol(troubleOrder.getSymbol())
                .orderId(troubleOrder.getOrderId())
                .build();

        CancelOrderResponseDto cancelOrderResponseDto = orderStockMarketService.cancelOrder(cancelOrderRequestDto);
        OrderEntity foundedOrderEntity = orderStorageService.getByOrderId(cancelOrderResponseDto.getOrderId()).orElseThrow(NoSuchEntityException::new);
        OrderDto canceledOrder = convertOrderEntityToOrderDto(foundedOrderEntity);
        canceledOrder.setStatus(EOrderStatus.CANCELED);
        OrderEntity updatedOrderEntity = orderStorageService.update(canceledOrder).orElseThrow();
        return convertOrderEntityToOrderDto(updatedOrderEntity);
    }

    @Override
    public OrderDto updateStatus(OrderDto orderDto, EOrderStatus conditionalStatus) {

        QueryOrderDto queryOrder = QueryOrderDto.builder()
                .symbol(orderDto.getSymbol())
                .orderId(orderDto.getOrderId())
                .build();

        EOrderStatus updatedStatus = orderStockMarketService.getOrderStatus(queryOrder);

        if (!orderDto.getStatus().equals(updatedStatus)) {

            if (updatedStatus.equals(conditionalStatus)) {
                orderDto.setStatus(updatedStatus);

                OrderEntity updatedOrderEntity = orderStorageService.update(orderDto).orElseThrow();
                return convertOrderEntityToOrderDto(updatedOrderEntity);

            }
        }

        return null;
    }

    @Override
    public List<OrderDto> getAllBySymbol(String symbol) {
        return orderStorageService.getAllBySymbol(symbol).orElseThrow(NoSuchEntityException::new).stream()
                .map(this::convertOrderEntityToOrderDto)
                .toList();
    }

    @Override
    public OrderDto getByOrderId(Long orderId) {
        OrderEntity foundedOrderEntity = orderStorageService.getByOrderId(orderId).orElseThrow(NoSuchEntityException::new);
        return convertOrderEntityToOrderDto(foundedOrderEntity);
    }

    @Override
    public OrderDto getByUuid(UUID uuid) {
        OrderEntity foundedOrderEntity = orderStorageService.getByUuid(uuid).orElseThrow(NoSuchEntityException::new);
        return convertOrderEntityToOrderDto(foundedOrderEntity);
    }

    @Override
    public OrderDto getBySymbolAndOrderId(String symbol, Long orderId) {
        OrderEntity foundedOrderEntity = orderStorageService.getBySymbolAndOrderId(symbol, orderId).orElseThrow(NoSuchEntityException::new);
        return convertOrderEntityToOrderDto(foundedOrderEntity);
    }


    private BigDecimal getAssetQuantityUsingStepSize(BigDecimal quantity, BigDecimal stepSize) {
        BigDecimal rest = quantity.remainder(stepSize);
        return quantity.subtract(rest);
    }

    private OrderEntity convertOrderDtoToOrderEntity(OrderDto source) {
        return conversionService.convert(source, OrderEntity.class);
    }

    private OrderDto convertOrderEntityToOrderDto(OrderEntity source) {
        return conversionService.convert(source, OrderDto.class);
    }

    private BigDecimal getValueFromPercentage(BigDecimal value, BigDecimal percent) {
        return value.multiply(percent).divide(new BigDecimal(100), RoundingMode.DOWN);
    }

    private BigDecimal getAssetPriceUsingStepSize(BigDecimal price, BigDecimal tickSize) {
        BigDecimal rest = price.remainder(tickSize);
        return price.subtract(rest);
    }


}
