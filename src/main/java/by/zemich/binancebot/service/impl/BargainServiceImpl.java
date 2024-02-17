package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.api.BargainDao;
import by.zemich.binancebot.DAO.entity.BargainEntity;
import by.zemich.binancebot.core.dto.BargainCreateDto;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.enums.EBargainStatus;
import by.zemich.binancebot.service.api.BargainService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BargainServiceImpl implements BargainService {

    private final BargainDao bargainDao;
    private final ModelMapper mapper;

    public BargainServiceImpl(BargainDao bargainDao, ModelMapper mapper) {
        this.bargainDao = bargainDao;
        this.mapper = mapper;
    }

    @Override
    public BargainEntity save(BargainDto bargainDto) {
        BargainEntity savedBargain = mapper.map(bargainDto, BargainEntity.class);
        return bargainDao.save(savedBargain);
    }

    @Override
    public Optional<BargainEntity> getByUuid(UUID uuid) {
        return bargainDao.findById(uuid);
    }

    @Override
    public List<BargainEntity> getAll() {
        return bargainDao.findAll();
    }

    @Override
    public List<BargainEntity> getAllByStatus(EBargainStatus status) {
        return bargainDao.findAllByStatus(status);
    }
    @Override
    public boolean existsBySymbolAndStatusLike(String symbol, EBargainStatus bargainStatus) {
        return bargainDao.existsBySymbolAndStatusLike(symbol, bargainStatus);
    }
    @Override
    public BargainEntity update(BargainDto bargainDto) {
        BargainEntity savedBargain = mapper.map(bargainDto, BargainEntity.class);
        return bargainDao.save(savedBargain);
    }

    @Override
    public void deleteByUuid(UUID uuid) {
      bargainDao.deleteById(uuid);
    }
}
