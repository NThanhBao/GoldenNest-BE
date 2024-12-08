package com.example.GoldenNest.service;

import com.example.GoldenNest.model.entity.Enum.OrderStatus;
import com.example.GoldenNest.model.entity.Enum.UserOrderStatus;
import com.example.GoldenNest.model.entity.Order;

public interface OrdersService {

    Order orderAllCart(String shippingAddress);

    Order orderByCartId(String cartId, String shippingAddress);

    Order updateOrderStatus(String orderId, OrderStatus newStatus);

    void cancelOrder(String orderId);
}
