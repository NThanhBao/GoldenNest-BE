package com.example.GoldenNest.repositories;

import com.example.GoldenNest.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findByCategoryId(String categoryId, Pageable pageable);

    @Modifying
    @Query(value = "INSERT INTO product_coupons (product_id, coupon_id) VALUES (:productId, :couponId)",
            nativeQuery = true)
    void addCouponToProduct(@Param("productId") String productId, @Param("couponId") String couponId);

    @Modifying
    @Query(value = "DELETE FROM product_coupons WHERE product_id = :productId AND coupon_id = :couponId",
            nativeQuery = true)
    int removeCouponFromProduct(@Param("productId") String productId, @Param("couponId") String couponId);

    @EntityGraph(attributePaths = {"category", "userId", "medias", "coupon"})
    @Query("SELECT p FROM Product p JOIN p.coupon c WHERE c.id = :couponId")
    List<Product> findProductsByCouponId(@Param("couponId") String couponId);
}