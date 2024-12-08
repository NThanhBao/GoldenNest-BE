package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.entity.*;
import com.example.GoldenNest.model.entity.Enum.OrderStatus;
import com.example.GoldenNest.model.entity.Enum.UserOrderStatus;
import com.example.GoldenNest.repositories.*;
import com.example.GoldenNest.service.OrdersService;
import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final CartRepository cartRepository;
    private final UsersRepository usersRepository;
    private final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);

    public OrdersServiceImpl(OrdersRepository orderRepository,
                            OrderDetailsRepository orderDetailsRepository,
                            CartRepository cartRepository,
                            UsersRepository usersRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.cartRepository = cartRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public Order orderAllCart(String shippingAddress) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }
        String userId = currentUser.getId();
        logger.info("Current user ID: {}", userId);

        List<Cart> userCart = cartRepository.findByUserId(userId, Pageable.unpaged()).getContent();
        if (userCart.isEmpty()) {
            throw new IllegalStateException("Cart is empty. Cannot place an order.");
        }

        double totalPrice = userCart.stream()
                .mapToDouble(cart -> cart.getProduct().getPrice() * cart.getQuantity())
                .sum();

        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setUser(currentUser);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        order.setUserOrderStatus(UserOrderStatus.PLACED);
        order.setShippingAddress(shippingAddress);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        for (Cart cart : userCart) {
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setId(UUID.randomUUID().toString());
            orderDetails.setOrder(order);
            orderDetails.setProduct(cart.getProduct());
            orderDetails.setQuantity(cart.getQuantity());
            orderDetails.setPrice(cart.getProduct().getPrice());
            orderDetailsRepository.save(orderDetails);
        }

        cartRepository.deleteAll(userCart);

        logger.info("Order created successfully for user {} with total price: {}", userId, totalPrice);
        return order;
    }

    @Override
    public Order orderByCartId(String cartId, String shippingAddress) {
        return null;
    }

    @Override
    public Order updateOrderStatus(String orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        switch (order.getStatus()) {
            case PENDING:
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalArgumentException("Chỉ có thể chuyển từ PENDING sang SHIPPED hoặc CANCELLED.");
                }
                break;

            case SHIPPED:
                if (newStatus != OrderStatus.IN_TRANSIT && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalArgumentException("Chỉ có thể chuyển từ SHIPPED sang IN_TRANSIT hoặc CANCELLED.");
                }
                break;

            case IN_TRANSIT:
                if (newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalArgumentException("Chỉ có thể chuyển từ IN_TRANSIT sang DELIVERED hoặc CANCELLED.");
                }
                break;

            case DELIVERED:
                throw new IllegalArgumentException("Đơn hàng đã được giao và không thể thay đổi trạng thái.");

            case CANCELLED:
                throw new IllegalArgumentException("Đơn hàng đã bị hủy và không thể thay đổi trạng thái.");

            default:
                throw new IllegalArgumentException("Trạng thái đơn hàng không hợp lệ.");
        }

        order.setStatus(newStatus);

        return orderRepository.save(order);
    }


    @Override
    public void cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        // Kiểm tra trạng thái đơn hàng trước khi hủy
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Không thể hủy đơn hàng đã giao hoặc đã hủy.");
        }

        // Cập nhật trạng thái của người dùng từ PLACED sang CANCELLED
        if (order.getUserOrderStatus() == UserOrderStatus.PLACED) {
            order.setUserOrderStatus(UserOrderStatus.CANCELLED);
        }

        // Lưu thay đổi
        orderRepository.save(order);
    }


}
