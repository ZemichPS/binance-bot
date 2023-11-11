package by.zemich.binancebot.service.converter.JsonComponents;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.OrderDto;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

public class BargainDtoToBargainEntityConverter implements Converter<BargainDto, BargainEntity> {
    @Override
    public BargainEntity convert(BargainDto source) {
        List<OrderEntity> entities = new ArrayList<>();

        for (OrderDto orderDto : source.getOrders()) {
            OrderEntity orderEntity = new OrderEntity();
            BeanUtils.copyProperties(orderDto, orderEntity);
            entities.add(orderEntity);
        }


        BargainEntity bargainEntity = new BargainEntity();
        bargainEntity.setUuid(source.getUuid());
        bargainEntity.setOrders(entities);
        bargainEntity.setPercentageResult(source.getPercentageResult());
        bargainEntity.setFinanceResult(source.getFinanceResult());
        bargainEntity.setTimeInWork(source.getTimeInWork());
        bargainEntity.setFinishTime(source.getFinishTime());
        bargainEntity.setStatus(source.getStatus());

        return bargainEntity;
    }
}
