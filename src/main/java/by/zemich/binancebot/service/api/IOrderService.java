package by.zemich.binancebot.service.api;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IOrderService {
    Optional<OrderEntity> create(NewOrderRequestDto newOrder);

    Optional<OrderEntity> cancel(CancelOrderRequestDto cancelOrderRequestDto);
    Optional<OrderEntity> update(CurrentOpenOrderResponseDto orderDto);
    Optional<OrderEntity> updateStatus(OrderEntity orderEntity);

    Optional<QueryOrderResponseDto> get(QueryOrderDto neededOrder);

    Optional<CurrentOpenOrderResponseDto> get(CurrentOpenOrderQueryDto openedOrder);

    Optional<List<CurrentOpenOrderResponseDto>> get(CurrentOpenOrdersQueryDto openedOrders);

    Optional<List<OrderEntity>> getAll();

    Optional<List<OrderEntity>> getBySymbol(String symbol);
    Optional<OrderEntity> updateByUuid(UUID uuid);

    Optional<OrderEntity> getByOrderId(Long orderId);

    Optional<OrderEntity> getBySymbolAndOrderId(String symbol, Long orderId);

    Optional<OrderEntity> getByUuid(UUID uuid);

    Optional<List<OrderEntity>> getAllOpened();






}
