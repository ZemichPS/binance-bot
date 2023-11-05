package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.service.api.IConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

public class JacksonConverterImpl implements IConverter {
    private final ObjectMapper mapper;

    public JacksonConverterImpl(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    @Override
    public Map<String, Object> dtoToMap(Object o) {
        Map<String, Object> map = mapper.convertValue(o, new TypeReference<Map<String, Object>>() {
        });

        return map;
    }
}
