package by.zemich.binancebot.service.api;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.*;

import java.util.List;
import java.util.Optional;

public interface IOrderService {
    Optional<OrderEntity> create(NewOrderDTO newOrder);

    Optional<CancelOrderResponseDto> cancel(CancelOrderDto canceledOrder);

    Optional<QueryOrderResponseDto> get(QueryOrderDto neededOrder);

    Optional<CurrentOpenOrderResponseDto> get(CurrentOpenOrderQueryDto openedOrder);

    Optional<List<CurrentOpenOrderResponseDto>> get(CurrentOpenOrdersQueryDto openedOrders);

    Optional<List<HistoricalOrderResponseDto>> getAll(HistoricalOrderQueryDto historicalOrderQuery);

}
