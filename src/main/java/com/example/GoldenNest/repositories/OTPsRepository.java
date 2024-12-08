package com.example.GoldenNest.repositories;


import com.example.GoldenNest.model.entity.OTPs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface OTPsRepository extends JpaRepository<OTPs, Long> {

    OTPs findByMailAndOtp(String mail, String otp);
    @Query("SELECT otp FROM OTPs otp JOIN FETCH otp.userId")
    Page<OTPs> findAllWithUser(Pageable pageable);

}