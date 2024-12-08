package com.example.GoldenNest.model.entity;

import com.example.GoldenNest.model.entity.Enum.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment {
    @Id
    @Column(length = 36)
    @NotNull
    @EqualsAndHashCode.Include
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

    public Payment() {
        this.id = UUID.randomUUID().toString();  // Khởi tạo UUID thủ công
    }

    // Getters and Setters
}
