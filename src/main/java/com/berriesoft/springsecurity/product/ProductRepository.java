package com.berriesoft.springsecurity.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    //Optional<User> findByEmail(String email);
    Optional<Product> findByProductCode(String productCode);

}
