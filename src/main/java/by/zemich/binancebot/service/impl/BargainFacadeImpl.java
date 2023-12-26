package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainCreateDto;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EBargainStatus;
import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.core.exeption.NoSuchEntityException;
import by.zemich.binancebot.service.api.BargainFacade;
import by.zemich.binancebot.service.api.IBargainStorageService;
import by.zemich.binancebot.service.api.OrderFacade;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BargainFacadeImpl implements BargainFacade {

    private final IBargainStorageService bargainStorageService;
    private final ConversionService conversionService;

    private final OrderFacade orderFacade;

    public BargainFacadeImpl(IBargainStorageService bargainStorageService, ConversionService conversionService, OrderFacade orderFacade) {
        this.bargainStorageService = bargainStorageService;
        this.conversionService = conversionService;
        this.orderFacade = orderFacade;
    }

    @Override
    public BargainDto create(BargainCreateDto bargainCreateDto) {
        BargainDto newBargainDto = new BargainDto();
        newBargainDto.setUuid(UUID.randomUUID());
        newBargainDto.setStatus(EBargainStatus.NEW);
        newBargainDto.setStrategy(bargainCreateDto.getStrategy());
        newBargainDto.setSymbol(bargainCreateDto.getSymbol().getSymbol());
        newBargainDto.setInterest(bargainCreateDto.getPercentageAim());
        BargainEntity savedBargainEntity = bargainStorageService.save(newBargainDto).orElseThrow();
        return convertBargainEntityToBargainDto(savedBargainEntity);
    }

    @Override
    public BargainDto update(BargainDto bargainDtoForUpdate) {
        BargainEntity updatedBargainEntity = bargainStorageService.update(bargainDtoForUpdate).orElseThrow();
        return convertBargainEntityToBargainDto(updatedBargainEntity);
    }

    @Override
    public BargainDto addBuyOrder(BargainDto bargainDto, OrderDto buyOrder) {
        return null;
    }

    @Override
    public BargainDto addSellOrder(BargainDto bargainDto, OrderDto sellOrder) {
        return null;
    }

    @Override
    public BargainDto endByReasonExpired(BargainDto bargainDto) {
        return null;
    }

    @Override
    public BargainDto finalize(BargainDto bargainDto, EBargainStatus status) {
        return null;
    }

    @Override
    public BargainDto updateResult(BargainDto bargainDto) {
        return null;
    }

    @Override
    public Optional<List<BargainDto>> checkOnFinish() {
        List<BargainDto> bargainDtoList = new ArrayList<>();

        bargainStorageService.getAllByStatus(EBargainStatus.OPEN_SELL_ORDER_CREATED).orElseThrow()
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
    public Optional<List<BargainDto>> getAllByStatus(EBargainStatus status) {
        return Optional.of(bargainStorageService.getAllByStatus(status).orElseThrow(NoSuchEntityException::new).stream()
                .map(this::convertBargainEntityToBargainDto)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<BargainDto>> getAllWithFilledBuyOrders() {
        return null;
    }

    @Override
    public BargainDto completeBargainByReasonTimeoutBuyOrder(BargainDto troubleBargain) {
        return null;
    }

    private BargainDto convertBargainEntityToBargainDto(BargainEntity source) {
        return conversionService.convert(source, BargainDto.class);
    }

}
