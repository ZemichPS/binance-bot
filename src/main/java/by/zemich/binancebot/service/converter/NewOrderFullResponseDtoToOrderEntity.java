package by.zemich.binancebot.service.converter;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.binance.NewOrderFullResponseDto;
import org.springframework.core.convert.converter.Converter;

public class NewOrderFullResponseDtoToOrderEntity implements Converter<NewOrderFullResponseDto, OrderEntity> {
    @Override
    public OrderEntity convert(NewOrderFullResponseDto source) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setSymbol(source.getSymbol());
        orderEntity.setOrderId(source.getOrderId());
        orderEntity.setOrderListId(source.getOrderListId());
        orderEntity.setClientOrderId(source.getClientOrderId());
        orderEntity.setTransactTime(source.getTransactTime());
        orderEntity.setPrice(source.getPrice());
        orderEntity.setOrigQty(source.getOrigQty());
        orderEntity.setExecutedQty(source.getExecutedQty());
        orderEntity.setCummulativeQuoteQty(source.getCummulativeQuoteQty());
        orderEntity.setStatus(source.getStatus());
        orderEntity.setTimeInForce(source.getTimeInForce());
        orderEntity.setType(source.getType());
        orderEntity.setSide(source.getSide());
        orderEntity.setWorkingTime(source.getWorkingTime());
        orderEntity.setSelfTradePreventionMode(source.getSelfTradePreventionMode());

        return orderEntity;

    }
}
