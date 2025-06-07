package com.siamak.shop.service;

import com.siamak.shop.model.Order;
import com.siamak.shop.model.User;
import com.siamak.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> retrieveOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    public void updateOrderStatus(Long orderId, String status) {
        int updated = orderRepository.updateOrderStatus(orderId, status);
        if (updated == 0) {
            // Order not found
        }
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
        System.out.println("DONE");
    }

}
