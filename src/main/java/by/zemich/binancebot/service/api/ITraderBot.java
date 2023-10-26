package by.zemich.binancebot.service.api;

public interface ITraderBot {
    void updateSeries();
    void lookForEnterPosition();

    void lookForExitPosition();

}
