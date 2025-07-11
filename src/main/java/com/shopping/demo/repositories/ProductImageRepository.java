package com.shopping.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopping.demo.entities.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
	List<ProductImage> findByProduct_ProductId(Integer productId);
}
