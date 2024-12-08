package com.example.GoldenNest.repositories;

import com.example.GoldenNest.model.entity.Enum.UserOrderStatus;
import com.example.GoldenNest.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrdersRepository extends JpaRepository<Order, String> {
    List<Order> findByUserOrderStatus(UserOrderStatus userOrderStatus);
}
