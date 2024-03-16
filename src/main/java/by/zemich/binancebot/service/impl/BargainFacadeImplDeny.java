package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainCreateDto;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EBargainStatus;
import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.service.api.BargainFacade;
import by.zemich.binancebot.service.api.BargainService;
import by.zemich.binancebot.service.api.OrderFacade;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BargainFacadeImplDeny implements BargainFacade {

    private final BargainService bargainServiceService;
    private final OrderFacade orderFacade;

    private final ModelMapper modelMapper;

    public BargainFacadeImplDeny(BargainService bargainServiceService, OrderFacade orderFacade, ModelMapper modelMapper) {
        this.bargainServiceService = bargainServiceService;
        this.orderFacade = orderFacade;
        this.modelMapper = modelMapper;
    }


    @Override
    public BargainDto create(BargainCreateDto bargainCreateDto) {
        BargainDto newBargainDto = BargainDto.builder()
                .uuid(UUID.randomUUID())
                .status(EBargainStatus.NEW)
                .strategy(bargainCreateDto.getStrategy())
                .symbol(bargainCreateDto.getAsset().getSymbol())
                .interest(bargainCreateDto.getPercentageAim())
                .build();

        BargainEntity savedBargainEntity = bargainServiceService.save(newBargainDto);
        return convertBargainEntityToBargainDto(savedBargainEntity);
    }

    @Override
    public BargainDto update(BargainDto bargainDtoForUpdate) {
        BargainEntity updatedBargainEntity = bargainServiceService.update(bargainDtoForUpdate);
        return convertBargainEntityToBargainDto(updatedBargainEntity);
    }

    @Override
    public BargainDto complete(BargainDto bargainDto, EBargainStatus status) {
        return null;
    }

    @Override
    public BargainDto updateResult(BargainDto bargainDto) {
        return null;
    }

    @Override
    public boolean checkOnFinish(BargainDto bargain) {
        return false;
    }

    @Override
    public List<BargainDto> checkOnFinish() {
        List<BargainDto> bargainDtoList = new ArrayList<>();

        bargainServiceService.getAllByStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED).orElseThrow()
                .stream().map(this::convertBargainEntityToBargainDto)
                .forEach(bargainDto -> {

                            if (Objects.nonNull(bargainDto.getBuyOrder()) && Objects.nonNull(bargainDto.getSellOrder())) {
                                OrderDto sellOrderDto = bargainDto.getSellOrder();

                                // сравниваем статусы
                                if (Objects.nonNull(orderFacade.updateStatus(sellOrderDto, EOrderStatus.FILLED))) {
                                    bargainDtoList.add(bargainDto);
                                }
                            }
                        }
                );
        return Optional.ofNullable(bargainDtoList);
    }

    @Override
    public List<BargainDto> getAllByStatus(EBargainStatus status) {
        return bargainServiceService.getAllByStatus(status).stream()
                .map(this::convertBargainEntityToBargainDto)
                .toList();
    }


    private BargainDto convertBargainEntityToBargainDto(BargainEntity source) {
        return modelMapper.map(source, BargainDto.class);
    }

}
