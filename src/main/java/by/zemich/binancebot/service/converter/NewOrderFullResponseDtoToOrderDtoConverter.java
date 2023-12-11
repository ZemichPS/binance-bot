package by.zemich.binancebot.service.converter;

import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.dto.binance.NewOrderFullResponseDto;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

public class NewOrderFullResponseDtoToOrderDtoConverter implements Converter<NewOrderFullResponseDto, OrderDto> {
    @Override
    public OrderDto convert(NewOrderFullResponseDto source) {

        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(source, orderDto);

        return orderDto;
    }
}
