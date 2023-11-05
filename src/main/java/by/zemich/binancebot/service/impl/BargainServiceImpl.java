package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.api.IBargainDao;
import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.enums.EBargainStatus;
import by.zemich.binancebot.service.api.IBargainService;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class BargainServiceImpl implements IBargainService {


    private final IBargainDao bargainDao;
    private final ConversionService conversionService;


    public BargainServiceImpl(IBargainDao bargainDao, ConversionService conversionService) {
        this.bargainDao = bargainDao;
        this.conversionService = conversionService;
    }

    @Override
    @Transactional
    public Optional<BargainEntity> save(BargainDto bargainDto) {
        BargainEntity bargainEntity = conversionService.convert(bargainDto, BargainEntity.class);
        return Optional.of(bargainEntity);
    }

    @Override
    @Transactional
    public Optional<BargainEntity> update(BargainDto bargainDto) {
        BargainEntity bargainEntity = conversionService.convert(bargainDto, BargainEntity.class);
        return Optional.of(bargainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<BargainEntity>> getAll() {
        List<BargainEntity> bargainEntities = bargainDao.findAll();
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
