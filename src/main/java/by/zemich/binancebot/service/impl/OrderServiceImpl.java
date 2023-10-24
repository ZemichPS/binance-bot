package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.api.IOrderDao;
import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.*;
import by.zemich.binancebot.service.api.IConverter;
import by.zemich.binancebot.service.api.IOrderService;
import by.zemich.binancebot.service.api.IStockMarketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements IOrderService {

    private final IConverter converter;
    private final ObjectMapper objectMapper;
    private final IOrderDao orderDao;
    private final ConversionService conversionService;
    private final IStockMarketService stockMarketService;

    public OrderServiceImpl(IConverter converter, ObjectMapper objectMapper, IOrderDao orderDao, ConversionService conversionService, IStockMarketService stockMarketService) {
        this.converter = converter;
        this.objectMapper = objectMapper;
        this.orderDao = orderDao;
        this.conversionService = conversionService;
        this.stockMarketService = stockMarketService;
    }


    @Override
    public Optional<OrderEntity> create(NewOrderDTO newOrder) {
        NewOrderFullResponseDto responseDto = stockMarketService.createOrder(converter.dtoToMap(newOrder)).orElseThrow(RuntimeException::new);
        OrderEntity entity = conversionService.convert(responseDto, OrderEntity.class);
        orderDao.save(entity);
        return Optional.of(entity);
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
        return Optional.ofNullable(null);
/*
        String result = spotClient.createTrade().getOrders(converter.dtoToMap(historicalOrderQuery));
        try {
            List<HistoricalOrderResponseDto> historicalOrderResponses = objectMappermapper.readValue(result, List.class);
            return Optional.of(historicalOrderResponses);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    */
    }


}
