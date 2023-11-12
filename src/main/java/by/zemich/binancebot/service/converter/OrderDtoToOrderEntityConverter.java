package by.zemich.binancebot.service.converter;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.OrderDto;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

public class OrderDtoToOrderEntityConverter implements Converter<OrderDto, OrderEntity> {
    @Override
    public OrderEntity convert(OrderDto source) {
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(source, orderEntity);
        return orderEntity;
    }
}
