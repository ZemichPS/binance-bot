package by.zemich.binancebot.service.api;

public interface ITraderBot {

    void lookForEnterPosition();

    void registerStrategy(String name, IStrategy strategyManager);

    void checkOrderStatus();





}
