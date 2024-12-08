package com.example.GoldenNest.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "OTPs")
@Getter
@Setter
public class OTPs {

    @Id
    @Column(length = 36)
    @NotNull
    @EqualsAndHashCode.Include
    private String id;

    @Column(unique = true)
    private String otp;

    @Column(nullable = false)
    private String mail;

    private Timestamp expirationTime;

    private Timestamp createAt;

    private boolean used;

    public OTPs() {
        this.id = UUID.randomUUID().toString();
    }

    @PrePersist
    protected void onCreate() {
        createAt = new Timestamp(new Date().getTime());
    }

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Users userId;

}
