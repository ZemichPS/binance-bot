package by.zemich.binancebot.endpoint.web.bot;

import by.zemich.binancebot.config.properties.TelegramProperties;
import by.zemich.binancebot.core.dto.EventDto;
import by.zemich.binancebot.service.api.Converter;
import by.zemich.binancebot.service.api.INotifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class TelegramBot extends TelegramLongPollingBot implements INotifier {
    private final TelegramProperties properties;
    private final Converter converter;

    public TelegramBot(TelegramProperties properties, Converter converter) {
        this.properties = properties;
        this.converter = converter;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);
    }

    @Override
    public String getBotUsername() {
        return properties.getName();
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }


    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    @Override
    public void notify(EventDto eventDto) {
        SendMessage smg = SendMessage.builder()
                .chatId(properties.getChatID())
                .text(eventDto.toString())
                .build();

        try {
            execute(smg);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }


}
