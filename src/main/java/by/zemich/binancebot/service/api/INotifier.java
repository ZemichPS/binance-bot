package by.zemich.binancebot.service.api;

import by.zemich.binancebot.core.dto.Event;

public interface INotifier {

    void notify(Event event);
}
