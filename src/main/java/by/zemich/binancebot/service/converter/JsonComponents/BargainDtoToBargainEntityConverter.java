package by.zemich.binancebot.service.converter.JsonComponents;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.BargainDto;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

public class BargainDtoToBargainEntityConverter implements Converter<BargainDto, BargainEntity> {
    @Override
    public BargainEntity convert(BargainDto source) {
        OrderEntity buyOrder = new OrderEntity();
        OrderEntity sellOrder = new OrderEntity();
        BeanUtils.copyProperties(source.getBuyOrder(), buyOrder);
        BeanUtils.copyProperties(source.getSellOrder(), sellOrder);

        BargainEntity bargainEntity = new BargainEntity();
        bargainEntity.setUuid(source.getUuid());
        bargainEntity.setBuyOrder(buyOrder);
        bargainEntity.setSellOrder(sellOrder);
        bargainEntity.setPercentageResult(source.getPercentageResult());
        bargainEntity.setFinanceResult(source.getFinanceResult());
        bargainEntity.setTimeInWork(source.getTimeInWork());
        bargainEntity.setFinishTime(source.getFinishTime());
        bargainEntity.setStatus(source.getStatus());

        return bargainEntity;
    }
}
