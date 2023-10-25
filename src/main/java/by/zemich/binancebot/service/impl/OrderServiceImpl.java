package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.api.IOrderDao;
import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.core.exeption.NoSuchEntityException;
import by.zemich.binancebot.service.api.IConverter;
import by.zemich.binancebot.service.api.IOrderService;
import by.zemich.binancebot.service.api.IStockMarketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements IOrderService {

    private final IConverter converter;
    private final ObjectMapper objectMapper;
    private final IOrderDao orderDao;
    private final ConversionService conversionService;
    private final IStockMarketService stockMarketService;

    public OrderServiceImpl(IConverter converter, ObjectMapper objectMapper, IOrderDao orderDao, ConversionService conversionService, IStockMarketService stockMarketService) {
        this.converter = converter;
        this.objectMapper = objectMapper;
        this.orderDao = orderDao;
        this.conversionService = conversionService;
        this.stockMarketService = stockMarketService;
    }


    @Override
    public Optional<OrderEntity> create(NewOrderRequestDto newOrder) {
        NewOrderFullResponseDto responseDto = stockMarketService.createOrder(converter.dtoToMap(newOrder)).orElseThrow(RuntimeException::new);
        OrderEntity entity = conversionService.convert(responseDto, OrderEntity.class);
        entity.setUuid(UUID.randomUUID());
        orderDao.save(entity);
        return Optional.of(entity);
    }

    @Transactional
    @Override
    public Optional<OrderEntity> cancel(CancelOrderRequestDto cancelOrderRequestDto) {

        CancelOrderResponseDto canceledOrder = stockMarketService.cancelOrder(converter.dtoToMap(cancelOrderRequestDto)).orElseThrow(RuntimeException::new);
        OrderEntity orderEntity =  orderDao.findByOrderId(canceledOrder.getOrderId()).orElseThrow(NoSuchEntityException::new);
        orderEntity.setStatus(EOrderStatus.CANCELED);
        orderDao.save(orderEntity);
        return Optional.of(orderEntity);
    }

    @Override
    public Optional<QueryOrderResponseDto> get(QueryOrderDto neededOrder) {
        return Optional.empty();
    }

    @Override
    public Optional<CurrentOpenOrderResponseDto> get(CurrentOpenOrderQueryDto openedOrder) {
        return Optional.empty();
    }

    @Override
    public Optional<List<CurrentOpenOrderResponseDto>> get(CurrentOpenOrdersQueryDto openedOrders) {
        return Optional.empty();
    }

    @Override
    public Optional<List<OrderEntity>> getAll() {

        List<OrderEntity> historicalOrderList = orderDao.findAll();
        return Optional.ofNullable(historicalOrderList);

    }

    @Override
    public Optional<List<OrderEntity>> getBySymbol(String symbol) {
        return Optional.empty();
    }

    @Override
    public Optional<List<OrderEntity>> getByOrderId(Long orderId) {
        return Optional.empty();
    }

    @Override
    public Optional<List<OrderEntity>> getBySymbolAndOrderId(String symbol, Long orderId) {
        return Optional.empty();
    }

    @Override
    public Optional<List<OrderEntity>> getByUuid(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<List<OrderEntity>> getOpened() {
        return Optional.empty();
    }



/*
    @Override
    public Optional<List<OrderEntity>> getAll(HistoricalOrderQueryDto historicalOrderQuery) {

        List<HistoricalOrderResponseDto> historicalOrderList = stockMarketService.getHistoricalOrderList(converter.dtoToMap(historicalOrderQuery)).orElseThrow(RuntimeException::new);
        return Optional.ofNullable(historicalOrderList);

    }
*/

}
