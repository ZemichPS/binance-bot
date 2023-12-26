package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.api.IOrderDao;
import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.core.exeption.NoSuchEntityException;
import by.zemich.binancebot.service.api.IConverter;
import by.zemich.binancebot.service.api.IOrderStorageService;
import by.zemich.binancebot.service.api.IStockMarketService;
import jakarta.transaction.Transactional;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderStorageServiceImpl implements IOrderStorageService {

    private final IConverter converter;
    private final IOrderDao orderDao;
    private final ConversionService conversionService;
    private final IStockMarketService stockMarketService;

    public OrderStorageServiceImpl(IConverter converter, IOrderDao orderDao, ConversionService conversionService, IStockMarketService stockMarketService) {
        this.converter = converter;
        this.orderDao = orderDao;
        this.conversionService = conversionService;
        this.stockMarketService = stockMarketService;
    }


    @Override
    @Transactional
    public Optional<OrderEntity> save(OrderDto newOrder) {
        OrderEntity newOrderEntity = conversionService.convert(newOrder, OrderEntity.class);
        orderDao.save(newOrderEntity);
        return Optional.of(newOrderEntity);
    }

    @Override
    public Optional<OrderEntity> update(OrderDto orderDto) {
        OrderEntity newOrderEntity = conversionService.convert(orderDto, OrderEntity.class);
        orderDao.save(newOrderEntity);
        return Optional.of(newOrderEntity);
    }

    @Override
    public Optional<OrderEntity>  updateStatus(OrderDto orderDto, EOrderStatus conditionalStatus) {

    }

    @Override
    public Optional<List<OrderEntity>> getAllBySymbol(String symbol) {
        List<OrderEntity> orderEntityList = orderDao.findBySymbol(symbol).orElseThrow(NoSuchEntityException::new);
        return Optional.of(orderEntityList);
    }


    @Override
    public Optional<OrderEntity> getByOrderId(Long orderId) {
        OrderEntity entity = orderDao.findByOrderId(orderId).orElseThrow(NoSuchEntityException::new);
        return Optional.of(entity);
    }

    @Override
    public Optional<OrderEntity> getBySymbolAndOrderId(String symbol, Long orderId) {
        OrderEntity entity = orderDao.findBySymbolAndOrderId(symbol, orderId).orElseThrow(NoSuchEntityException::new);
        return Optional.of(entity);
    }

    @Override
    public Optional<OrderEntity> getByUuid(UUID uuid) {
        OrderEntity entity = orderDao.findByUuid(uuid).orElseThrow(NoSuchEntityException::new);
        return Optional.of(entity);
    }





}
