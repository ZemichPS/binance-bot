package by.zemich.binancebot.service.util;

import by.zemich.binancebot.service.api.Converter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component("jsonConverter")
public class JsonConverter implements Converter {

    private final ObjectMapper mapper;

    public JsonConverter(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    @Override
    public Map<String, Object> dtoToMap(Object o) {
        Map<String, Object> map = mapper.convertValue(o, new TypeReference<Map<String, Object>>() {
        });

        return map;
    }
}
