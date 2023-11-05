package by.zemich.binancebot.config;
import by.zemich.binancebot.service.converter.BarDtoToBaseBarConverter;
import by.zemich.binancebot.service.converter.JsonComponents.BargainDtoToBargainEntityConverter;
import by.zemich.binancebot.service.converter.NewOrderFullResponseDtoToOrderEntity;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new BarDtoToBaseBarConverter());
        registry.addConverter(new NewOrderFullResponseDtoToOrderEntity());
        registry.addConverter(new BargainDtoToBargainEntityConverter());
     //   registry.addFormatter(new LocalDateTimeToMilliFormatter());
    }
}
