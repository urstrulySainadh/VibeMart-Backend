package com.shopping.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import com.shopping.demo.entities.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Integer>{
	
	// Custom query to count cart items for a given userId
	@Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItem c where c.user.userId = :userId")
	int countTotalItems(int userId);

}
