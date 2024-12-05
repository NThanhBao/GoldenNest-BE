package com.example.GoldenNest.repositories;

import com.example.GoldenNest.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    Users findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByMail(String mail);

    boolean existsByPhoneNumber(String phoneNumber);
}