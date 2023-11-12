package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.api.IBargainDao;
import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.enums.EBargainStatus;
import by.zemich.binancebot.core.enums.ESide;
import by.zemich.binancebot.service.api.IBargainService;
import by.zemich.binancebot.service.api.IOrderService;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BargainServiceImpl implements IBargainService {


    private final IBargainDao bargainDao;
    private final ConversionService conversionService;

    private final IOrderService orderService;


    public BargainServiceImpl(IBargainDao bargainDao, ConversionService conversionService, IOrderService orderService) {
        this.bargainDao = bargainDao;
        this.conversionService = conversionService;
        this.orderService = orderService;
    }

    @Override
    @Transactional
    public Optional<BargainEntity> create(BargainDto bargainDto) {

        BargainEntity newBargainEntity = conversionService.convert(bargainDto, BargainEntity.class);
        BargainEntity savedBargainEntity = bargainDao.save(newBargainEntity);
        return Optional.of(savedBargainEntity);
    }

    @Override
    @Transactional
    public Optional<BargainEntity> update(BargainDto bargainDto) {
        BargainEntity newBargainEntity = conversionService.convert(bargainDto, BargainEntity.class);
        BargainEntity savedBargainEntity = bargainDao.save(newBargainEntity);
        return Optional.of(savedBargainEntity);
    }

    @Override
    public Optional<BargainEntity> end(BargainDto bargainDto) {
        // тут всякие расчёты и просчёты

        BargainEntity bargainEntity = conversionService.convert(bargainDto, BargainEntity.class);
        BargainEntity savedEntity = bargainDao.save(bargainEntity);


        return Optional.of(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<BargainEntity>> getAll() {
        List<BargainEntity> bargainEntities = bargainDao.findAll();
        return Optional.of(bargainEntities);
    }

    @Override
    public Optional<List<BargainEntity>> updateOpenStatus() {
        List<BargainEntity> bargainEntities = new ArrayList<>();

        bargainDao.findAllByStatus(EBargainStatus.OPEN).forEach(bargainEntity -> {

                    if (bargainEntity.getOrders() != null && bargainEntity.getOrders().size() == 1) {

                        OrderDto orderDto = conversionService.convert(bargainEntity.getOrders().get(0), OrderDto.class);
                        // сравниваем статусы
                        if (orderService.updateStatus(orderDto).isPresent()) {
                            bargainEntities.add(bargainEntity);
                        }
                    }
                }
        );
        return Optional.of(bargainEntities);
    }

    @Override
    public Optional<List<BargainEntity>> checkOnFinish() {
        List<BargainEntity> bargainEntities = new ArrayList<>();

        bargainDao.findAllByStatus(EBargainStatus.OPEN_BUY_ORDER_FILLED).forEach(bargainEntity -> {

                    if (bargainEntity.getOrders() != null && bargainEntity.getOrders().size() == 2) {
                        OrderEntity orderEntity = bargainEntity.getOrders().stream()
                                .filter(entity -> entity.getSide().equals(ESide.SELL))
                                .findFirst()
                                .get();
                        OrderDto orderDto = conversionService.convert(orderEntity, OrderDto.class);
                        // сравниваем статусы
                        if (orderService.updateStatus(orderDto).isPresent()) {
                            bargainEntities.add(bargainEntity);
                        }
                    }
                }
        );
        return Optional.of(bargainEntities);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<BargainEntity>> getAllByStatus(EBargainStatus status) {
        List<BargainEntity> bargainEntities = bargainDao.findAllByStatus(status);
        return Optional.of(bargainEntities);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BargainEntity> getByUuid(UUID uuid) {
        return Optional.of(bargainDao.findById(uuid).get());
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        bargainDao.deleteById(uuid);
    }


}
