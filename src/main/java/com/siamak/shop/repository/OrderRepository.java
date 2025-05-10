package com.siamak.shop.repository;

import com.siamak.shop.model.Order;
import com.siamak.shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
