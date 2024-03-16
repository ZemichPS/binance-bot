package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.dto.OrderDto;
import by.zemich.binancebot.core.dto.binance.*;
import by.zemich.binancebot.core.enums.*;
import by.zemich.binancebot.core.exeption.NoSuchEntityException;
import by.zemich.binancebot.service.api.*;
import by.zemich.binancebot.service.api.OrderBrokerService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderFacadeImpl implements OrderFacade {

    private final OrderBrokerService orderBrokerService;
    private final OrderStorageService orderStorageService;
    private final ModelMapper modelMapper;

    public OrderFacadeImpl(OrderBrokerService orderBrokerService,
                           OrderStorageService orderStorageService,
                           ModelMapper modelMapper
    ) {
        this.orderBrokerService = orderBrokerService;
        this.orderStorageService = orderStorageService;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderDto createBuyLimitOrderByCurrentPrice(RequestForNewOrderDto requestForNewOrderDto) {
        OrderDto newCreatedOrder = orderBrokerService.createOrder(requestForNewOrderDto);
        OrderEntity savedOrderEntity = orderStorageService.save(newCreatedOrder);
        return convertOrderEntityToOrderDto(savedOrderEntity);
    }

    @Override
    public OrderDto createSellLimitOrder(RequestForNewOrderDto requestForNewOrderDto) {

        OrderDto newCreatedOrder = orderBrokerService.createOrder(requestForNewOrderDto);
        OrderEntity savedOrderEntity = orderStorageService.save(newCreatedOrder);
        return convertOrderEntityToOrderDto(savedOrderEntity);

    }

    @Override
    public OrderDto createSellLimitOrderByAskPrice(RequestForNewOrderDto requestForNewOrderDto) {
        OrderDto newCreatedOrder = orderBrokerService.createOrder(requestForNewOrderDto);
        OrderEntity savedOrderEntity = orderStorageService.save(newCreatedOrder);
        return convertOrderEntityToOrderDto(savedOrderEntity);
    }

    @Override
    public OrderDto createSellOrderByMarketPrice(RequestForNewOrderDto requestForNewSellOrder) {
        OrderDto newCreatedOrder = orderBrokerService.createOrder(requestForNewSellOrder);
        OrderEntity savedOrderEntity = orderStorageService.save(newCreatedOrder);
        return convertOrderEntityToOrderDto(savedOrderEntity);
    }

    @Override
    public OrderDto createStopLimitOrder(OrderDto order) {
        return null;
    }

    @Override
    public OrderDto cancelOrder(CancelOrderRequestDto cancelOrderRequest) {

        CancelOrderResponseDto cancelOrderResponseDto = orderBrokerService.cancelOrder(cancelOrderRequest);
        OrderEntity foundedOrderEntity = orderStorageService.getByOrderId(cancelOrderResponseDto.getOrderId()).orElseThrow(NoSuchEntityException::new);
        OrderDto canceledOrder = convertOrderEntityToOrderDto(foundedOrderEntity);
        canceledOrder.setStatus(EOrderStatus.CANCELED);
        OrderEntity updatedOrderEntity = orderStorageService.update(canceledOrder);
        return convertOrderEntityToOrderDto(updatedOrderEntity);
    }

    @Override
    public OrderDto updateStatus(OrderDto orderDto, EOrderStatus expectedStatus) {

        QueryOrderDto queryOrder = QueryOrderDto.builder()
                .symbol(orderDto.getSymbol())
                .orderId(orderDto.getOrderId())
                .build();

        EOrderStatus updatedStatus = orderBrokerService.getOrderStatus(queryOrder);

        if (!orderDto.getStatus().equals(updatedStatus)) {
            if (updatedStatus.equals(expectedStatus)) {
                orderDto.setStatus(updatedStatus);
                OrderEntity updatedOrderEntity = orderStorageService.update(orderDto);
                return convertOrderEntityToOrderDto(updatedOrderEntity);
            }
        }
        // TODO ИСПРАВИТЬ (НЕЛЬЗЯ ВОЗВРАЩАТЬ NULL)
        return null;
    }

    @Override
    public List<OrderDto> getAllBySymbol(String symbol) {
        return orderStorageService.getAllBySymbol(symbol).stream()
                .map(this::convertOrderEntityToOrderDto)
                .toList();
    }

    @Override
    public OrderDto getByOrderId(Long orderId) {
        OrderEntity foundedOrderEntity = orderStorageService.getByOrderId(orderId).orElseThrow(NoSuchEntityException::new);
        return convertOrderEntityToOrderDto(foundedOrderEntity);
    }

    @Override
    public OrderDto getByUuid(UUID uuid) {
        OrderEntity foundedOrderEntity = orderStorageService.getByUuid(uuid).orElseThrow(NoSuchEntityException::new);
        return convertOrderEntityToOrderDto(foundedOrderEntity);
    }

    @Override
    public OrderDto getBySymbolAndOrderId(String symbol, Long orderId) {
        OrderEntity foundedOrderEntity = orderStorageService.getBySymbolAndOrderId(symbol, orderId).orElseThrow(NoSuchEntityException::new);
        return convertOrderEntityToOrderDto(foundedOrderEntity);
    }


    private OrderDto convertOrderEntityToOrderDto(OrderEntity source) {
        return modelMapper.map(source, OrderDto.class);
    }




}
