package by.zemich.binancebot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class TelegramBotConfig {
    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(telegramBot);
        return api;
    }

    @Bean
    public List<BotCommand> listOfBotCommand(){
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "star work with bor"));
        commandList.add(new BotCommand("/registration", "star work with bot"));
        commandList.add(new BotCommand("/about", "information about bot"));
        return commandList;
    }
}
