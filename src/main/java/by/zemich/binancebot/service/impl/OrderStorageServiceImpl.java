package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.api.OrderDao;
import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.service.api.OrderStorageService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderStorageServiceImpl implements OrderStorageService {
    private final OrderDao orderDao;
    private final ModelMapper mapper;

    public OrderStorageServiceImpl(OrderDao orderDao, ModelMapper mapper) {
        this.orderDao = orderDao;
        this.mapper = mapper;
    }


    @Override
    @Transactional
    public OrderEntity save(OrderDto newOrder) {
        OrderEntity newOrderEntity =  mapper.map(newOrder, OrderEntity.class);
        return orderDao.save(newOrderEntity);
    }

    @Override
    public OrderEntity update(OrderDto order) {
        OrderEntity newOrderEntity =  mapper.map(order, OrderEntity.class);
        return orderDao.save(newOrderEntity);
    }


    @Override
    public List<OrderEntity> getAllBySymbol(String symbol) {
        return orderDao.findAllBySymbol(symbol);
    }


    @Override
    public Optional<OrderEntity> getByOrderId(Long orderId) {
        return orderDao.findByOrderId(orderId);
    }

    @Override
    public Optional<OrderEntity> getBySymbolAndOrderId(String symbol, Long orderId) {
        return orderDao.findBySymbolAndOrderId(symbol, orderId);
    }

    @Override
    public Optional<OrderEntity> getByBargain(BargainDto bargainDto) {
        BargainEntity bargain = mapper.map(bargainDto, BargainEntity.class);
        return orderDao.findByBargain(bargain);
    }

    @Override
    public Optional<OrderEntity> getByUuid(UUID uuid) {
        return orderDao.findByUuid(uuid);
    }

}
