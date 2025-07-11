package com.shopping.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopping.demo.entities.OrderItem;

@Repository
public interface OrderItemsRepository  extends JpaRepository<OrderItem, Integer>{
	 
	@Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderId = :orderId")
	 List<OrderItem> findByOrderId(String orderId);
	    
	    
	@Query("SELECT oi FROM OrderItem oi WHERE oi.order.userId = :userId AND oi.order.status = 'SUCCESS'")
	 List<OrderItem> findSuccessfulOrderItemsByUserId(int userId);
}
