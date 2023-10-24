package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.service.api.IConverter;
import by.zemich.binancebot.service.api.IOrderService;
import com.binance.connector.client.SpotClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements IOrderService {

    private final IConverter converter;
    private final ObjectMapper objectMappermapper;
    private final SpotClient spotClient;

    public OrderServiceImpl(IConverter converter, ObjectMapper objectMappermapper, SpotClient spotClient) {
        this.converter = converter;
        this.objectMappermapper = objectMappermapper;
        this.spotClient = spotClient;
    }


    @Override
    public Optional<NewOrderFullResponseDto> create(NewOrderDTO newOrder) {


        return Optional.empty();
    }

    @Override
    public Optional<CancelOrderResponseDto> cancel(CancelOrderDto canceledOrder) {
        return Optional.empty();
    }

    @Override
    public Optional<QueryOrderResponseDto> get(QueryOrderDto neededOrder) {
        return Optional.empty();
    }

    @Override
    public Optional<CurrentOpenOrderResponseDto> get(CurrentOpenOrderQueryDto openedOrder) {
        return Optional.empty();
    }

    @Override
    public Optional<List<CurrentOpenOrderResponseDto>> get(CurrentOpenOrdersQueryDto openedOrders) {
        return Optional.empty();
    }

    @Override
    public Optional<List<HistoricalOrderResponseDto>> getAll(HistoricalOrderQueryDto historicalOrderQuery) {

        String result = spotClient.createTrade().getOrders(converter.dtoToMap(historicalOrderQuery));
        try {
            List<HistoricalOrderResponseDto> historicalOrderResponses = objectMappermapper.readValue(result, List.class);
            return Optional.of(historicalOrderResponses);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
