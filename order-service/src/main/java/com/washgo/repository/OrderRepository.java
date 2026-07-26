package com.washgo.repository;

import com.washgo.common.enums.OrderStatus;
import com.washgo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByLaundryPartnerId(Long laundryPartnerId);

    List<Order> findByOrderStatus(OrderStatus orderStatus);

    boolean existsByOrderNumber(String orderNumber);
}