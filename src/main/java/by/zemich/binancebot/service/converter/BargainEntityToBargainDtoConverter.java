package by.zemich.binancebot.service.converter;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.OrderDto;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

public class BargainEntityToBargainDtoConverter implements Converter<BargainEntity, BargainDto> {
    @Override
    public BargainDto convert(BargainEntity source) {

        List<OrderDto> orderDtoList = new ArrayList<>();

        if (source.getOrders() != null) {
            source.getOrders().stream().forEach(orderEntity -> {
                OrderDto orderDto = new OrderDto();
                BeanUtils.copyProperties(orderEntity, orderDto);
                orderDtoList.add(orderDto);
            });
        }
        BargainDto bargainDto = new BargainDto();
        bargainDto.setUuid(source.getUuid());
        bargainDto.setDtCreate(source.getDtCreate());
        bargainDto.setDtUpdate(source.getDtUpdate());
        bargainDto.setOrders(orderDtoList);
        bargainDto.setPercentageResult(source.getPercentageResult());
        bargainDto.setFinanceResult(source.getFinanceResult());
        bargainDto.setTimeInWork(source.getTimeInWork());
        bargainDto.setFinishTime(source.getFinishTime());
        bargainDto.setStatus(source.getStatus());
        bargainDto.setSymbol(source.getSymbol());
        bargainDto.setCurrentFinanceResult(source.getCurrentFinanceResult());
        bargainDto.setCurrentPercentageResult(source.getCurrentPercentageResult());

        return bargainDto;
    }
}
