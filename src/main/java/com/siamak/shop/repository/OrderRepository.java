package com.siamak.shop.repository;

import com.siamak.shop.model.Order;
import com.siamak.shop.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :orderId")
    int updateOrderStatus(Long orderId, String status);

    void deleteOrderById(Long orderId);
}
