package by.zemich.binancebot.service.converter;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.OrderDto;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BargainDtoToBargainEntityConverter implements Converter<BargainDto, BargainEntity> {
    @Override
    public BargainEntity convert(BargainDto source) {

        BargainEntity bargainEntity = new BargainEntity();
        bargainEntity.setUuid(source.getUuid());
        bargainEntity.setDtCreate(source.getDtCreate());
        bargainEntity.setDtUpdate(source.getDtUpdate());
        bargainEntity.setPercentageResult(source.getPercentageResult());
        bargainEntity.setFinanceResult(source.getFinanceResult());
        bargainEntity.setTimeInWork(source.getTimeInWork());
        bargainEntity.setFinishTime(source.getFinishTime());
        bargainEntity.setStatus(source.getStatus());
        bargainEntity.setStrategy(source.getStrategy());
        bargainEntity.setSymbol(source.getSymbol());
        bargainEntity.setInterest(source.getInterest());


        if(Objects.nonNull(source.getBuyOrder())){
            OrderEntity buyOrderEntity = new OrderEntity();
            BeanUtils.copyProperties(source.getBuyOrder() , buyOrderEntity);
            bargainEntity.setBuyOrder(buyOrderEntity);
        }

        if(Objects.nonNull(source.getSellOrder())){
            OrderEntity sellOrderEntity = new OrderEntity();
            BeanUtils.copyProperties(source.getSellOrder() , sellOrderEntity);
            bargainEntity.setSellOrder(sellOrderEntity);
        }

        return bargainEntity;
    }
}
