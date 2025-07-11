package com.shopping.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopping.demo.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{
	Optional<Category> findByCategoryName(String categoryName);
}
