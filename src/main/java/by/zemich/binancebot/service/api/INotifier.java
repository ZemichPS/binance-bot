package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.EventDto;

public interface INotifier {

    void notify(EventDto eventDto);
}
