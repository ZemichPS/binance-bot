package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.dto.binance.Asset;
import by.zemich.binancebot.core.enums.EOrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface OrderFacade {
    OrderDto createBuyLimitOrderByCurrentPrice(Asset assetForBuying, BigDecimal deposit);
    OrderDto createSellLimitOrder(OrderDto buyOrder, BigDecimal percentageAim);
    OrderDto createSellOrderByAscPrice(OrderDto orderDtoToSell);
    OrderDto createSellOrderByMarketPrice(OrderDto orderDtoToSell);
    OrderDto createStopLimitOrder(OrderDto order);
    OrderDto cancelOrder(OrderDto troubleOrder);
    OrderDto updateStatus(OrderDto orderDto, EOrderStatus conditionalStatus);
    List<OrderDto> getAllBySymbol(String symbol);
    OrderDto getByOrderId(Long orderId);
    OrderDto getByUuid(UUID uuid);
    OrderDto getBySymbolAndOrderId(String symbol, Long orderId);

}
