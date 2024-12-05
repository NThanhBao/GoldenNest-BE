package com.example.GoldenNest.model.entity;

import com.example.GoldenNest.model.entity.Enum.PaymentType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "payment_date", updatable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentType paymentType;


    // Getters and Setters
}
