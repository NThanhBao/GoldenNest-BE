package com.example.GoldenNest.controller;

import com.example.GoldenNest.model.entity.Enum.OrderStatus;
import com.example.GoldenNest.model.entity.Order;
import com.example.GoldenNest.service.OrdersService;
import com.example.GoldenNest.util.annotation.CheckLogin;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrdersService orderService;

    public OrdersController(OrdersService orderService) {
        this.orderService = orderService;
    }

    @CheckLogin
    @PostMapping
    public ResponseEntity<Order> orderAllCart(@RequestParam String shippingAddress) {
        Order order = orderService.orderAllCart(shippingAddress);
        return ResponseEntity.ok(order);
    }

    @CheckLogin
    @PostMapping("/{cartId}")
    public ResponseEntity<Order> orderByCartId(@PathVariable String cartId,
                                               @RequestParam String shippingAddress) {
        try {
            Order order = orderService.orderByCartId(cartId, shippingAddress);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @CheckLogin
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String orderId, @RequestParam OrderStatus status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi hệ thống.");
        }
    }

    @CheckLogin
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable String orderId) {
        try {
            orderService.cancelOrder(orderId);
            return ResponseEntity.ok("Đơn hàng đã bị hủy.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại đơn hàng với ID: " + orderId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình hủy đơn hàng.");
        }
    }


}