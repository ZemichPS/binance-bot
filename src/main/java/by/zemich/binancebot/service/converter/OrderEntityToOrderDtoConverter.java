package by.zemich.binancebot.service.converter;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.OrderDto;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

public class OrderEntityToOrderDtoConverter implements Converter<OrderEntity, OrderDto> {
    @Override
    public OrderDto convert(OrderEntity source) {
        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(source, orderDto);
        return orderDto;
    }
}
