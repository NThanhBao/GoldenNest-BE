package com.example.GoldenNest.repositories;

import com.example.GoldenNest.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    Users findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByMail(String mail);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("select u from Users u where u.mail = :email")
    Users findByEmail(@Param("email") String email);

}