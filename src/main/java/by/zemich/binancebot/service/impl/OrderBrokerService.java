package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.service.api.Converter;
import com.binance.connector.client.SpotClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderBrokerService implements by.zemich.binancebot.service.api.OrderBrokerService {
    private final SpotClient spotClient;
    private final ObjectMapper objectMapper;
    private final ModelMapper mapper;
    private final Converter converter;

    public OrderBrokerService(SpotClient spotClient,
                              ObjectMapper objectMapper,
                              ModelMapper mapper, @Qualifier("reflectionConverter") Converter converter) {
        this.spotClient = spotClient;
        this.objectMapper = objectMapper;
        this.mapper = mapper;
        this.converter = converter;
    }


    @Override
    public OrderDto createOrder(RequestForNewOrderDto requestForNewOrderDto) {
        Map<String, Object> newOrderRequest = converter.dtoToMap(requestForNewOrderDto);
        String responseResult = spotClient.createTrade().newOrder(newOrderRequest);
        try {
            NewOrderFullResponseDto orderFullResponseDto = objectMapper.readValue(responseResult, NewOrderFullResponseDto.class);
            OrderDto createdOrderDto =  mapper.map(orderFullResponseDto, OrderDto.class);
            createdOrderDto.setUuid(UUID.randomUUID());
            return createdOrderDto;
        } catch (Exception exception) {
            throw new RuntimeException(exception.getCause());
        }
    }

    @Override
    public CancelOrderResponseDto cancelOrder(CancelOrderRequestDto cancelOrderRequestDto) {
        String responseResult = spotClient.createTrade().cancelOrder(converter.dtoToMap(cancelOrderRequestDto));
        try {
            return objectMapper.readValue(responseResult, CancelOrderResponseDto.class);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public EOrderStatus getOrderStatus(QueryOrderDto queryOrder) {
        try {
            String result = spotClient.createTrade().getOrders(converter.dtoToMap(queryOrder));
            List<EOrderStatus> statuses = this.convertStringResponseToOrderStatuses(result);
            if (statuses.isEmpty()) throw new RuntimeException("Failed to retrieve order status.");

            return statuses.get(0);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public List<HistoricalOrderResponseDto> getHistoricalOrderList(Map<String, Object> params) {
        String responseResult = spotClient.createTrade().getOrders(converter.dtoToMap(params));
        try {
            List<HistoricalOrderResponseDto> historicalOrderResponses = objectMapper.readValue(responseResult, List.class);
            return historicalOrderResponses;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



    private List<EOrderStatus> convertStringResponseToOrderStatuses(String response) {
        List<EOrderStatus> statuses = new ArrayList<>();
        List<Object> objectList = new JacksonJsonParser().parseList(response);
        objectList.forEach(object -> {

            LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) object;
            QueryOrderResponseDto orderResponse = new QueryOrderResponseDto();
            EOrderStatus status = EOrderStatus.valueOf(map.get("status"));
            statuses.add(status);
        });

        return statuses;
    }




}


