package by.zemich.binancebot.service.converter;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.CurrentOpenOrderResponseDto;
import org.springframework.core.convert.converter.Converter;

public class CurrentOpenOrderResponseDtoToOrderEntityConverter implements Converter<CurrentOpenOrderResponseDto, OrderEntity> {

    @Override
    public OrderEntity convert(CurrentOpenOrderResponseDto source) {
        OrderEntity entity = new OrderEntity();
        entity.setSymbol(source.getSymbol());
        entity.setOrderId(source.getOrderId());


        return null;
    }
}
