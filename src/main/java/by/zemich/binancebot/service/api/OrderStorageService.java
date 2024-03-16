package by.zemich.binancebot.service.api;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderStorageService {
    OrderEntity save(OrderDto newOrder);
    OrderEntity update(OrderDto order);
    List<OrderEntity> getAllBySymbol(String symbol);
    Optional<OrderEntity> getByOrderId(Long orderId);
    Optional<OrderEntity> getByUuid(UUID uuid);
    Optional<OrderEntity> getBySymbolAndOrderId(String symbol, Long orderId);
    Optional<OrderEntity> getByBargain(BargainDto bargainDto);

}
