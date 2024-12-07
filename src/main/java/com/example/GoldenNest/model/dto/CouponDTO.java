package com.example.GoldenNest.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CouponDTO {


    @NotNull
    @Size(min = 1, max = 50, message = "Code must be between 1 and 50 characters")
    private String code;

    @NotNull
    @Positive(message = "Discount amount must be a positive value")
    private Double discountAmount;

    @NotNull
    private LocalDate expiryDate;

    @NotNull
    private Boolean isActive;

    // Getters and Setters

}
