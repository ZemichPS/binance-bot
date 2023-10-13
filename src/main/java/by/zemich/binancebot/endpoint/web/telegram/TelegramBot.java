package by.zemich.binancebot.endpoint.web.telegram;

import by.zemich.binancebot.config.TelegramBotConfig;
import by.zemich.binancebot.config.properties.TelegramProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.util.List;

@Component
public class TelegramBot implements LongPollingBot {
    private final TelegramProperties  telegramProperties;
    private final List<BotCommand> commandList;
    private final TelegramBotConfig botConfig;

    public TelegramBot(TelegramProperties telegramProperties, List<BotCommand> commandList, TelegramBotConfig botConfig) {
        this.telegramProperties = telegramProperties;
        this.commandList = commandList;
        this.botConfig = botConfig;
    }


    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public BotOptions getOptions() {
        return null;
    }

    @Override
    public void clearWebhook() throws TelegramApiRequestException {

    }

    @Override
    public String getBotUsername() {
        return "MTCCurrencyBot";
    }

    @Override
    public String getBotToken() {
        return telegramProperties.getApiKey();
    }
}
