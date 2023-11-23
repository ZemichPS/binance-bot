package by.zemich.binancebot.service.api;

public interface ITraderBot {

    void lookForEnterPosition();

    void registerStrategy(String name, IRule strategyManager);

    void checkBargain();





}
