package by.zemich.binancebot.service.api;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.enums.EOrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IOrderStorageService {
    Optional<OrderEntity> save(OrderDto newOrder);
    Optional<OrderEntity> update(OrderDto orderDto);
    Optional<List<OrderEntity>> getAllBySymbol(String symbol);
    Optional<OrderEntity> getByOrderId(Long orderId);
    Optional<OrderEntity> getByUuid(UUID uuid);
    Optional<OrderEntity> getBySymbolAndOrderId(String symbol, Long orderId);

}
