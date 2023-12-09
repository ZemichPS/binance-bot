package by.zemich.binancebot.service.converter;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.OrderDto;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BargainEntityToBargainDtoConverter implements Converter<BargainEntity, BargainDto> {
    @Override
    public BargainDto convert(BargainEntity source) {

        BargainDto bargainDto = new BargainDto();
        bargainDto.setUuid(source.getUuid());
        bargainDto.setDtCreate(source.getDtCreate());
        bargainDto.setDtUpdate(source.getDtUpdate());
        bargainDto.setStrategy(source.getStrategy());
        bargainDto.setPercentageResult(source.getPercentageResult());
        bargainDto.setFinanceResult(source.getFinanceResult());
        bargainDto.setTimeInWork(source.getTimeInWork());
        bargainDto.setFinishTime(source.getFinishTime());
        bargainDto.setStatus(source.getStatus());
        bargainDto.setSymbol(source.getSymbol());
        bargainDto.setCurrentFinanceResult(source.getCurrentFinanceResult());
        bargainDto.setCurrentPercentageResult(source.getCurrentPercentageResult());

        if(Objects.nonNull(source.getBuyOrder())) {
            OrderDto buyOrderDto = new OrderDto();
            BeanUtils.copyProperties(source.getBuyOrder(), buyOrderDto);
            bargainDto.setBuyOrder(buyOrderDto);

        }

        if(Objects.nonNull(source.getSellOrder())) {
            OrderDto sellOrderDto = new OrderDto();
            BeanUtils.copyProperties(source.getSellOrder(), sellOrderDto);
            bargainDto.setBuyOrder(sellOrderDto);

        }

        return bargainDto;
    }
}
