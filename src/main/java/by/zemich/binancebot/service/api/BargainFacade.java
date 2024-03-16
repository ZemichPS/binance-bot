package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.BargainCreateDto;
import by.zemich.binancebot.core.dto.BargainDto;
import by.zemich.binancebot.core.enums.EBargainStatus;

import java.util.List;

public interface BargainFacade {
    BargainDto create(BargainCreateDto bargainCreateDto);
    BargainDto update(BargainDto bargainDtoForUpdate);
    BargainDto complete(BargainDto bargainDto, EBargainStatus status);
    BargainDto updateResult(BargainDto bargainDto);
    boolean checkOnFinish(BargainDto bargain);
    List<BargainDto> getAllByStatus(EBargainStatus status);
}
