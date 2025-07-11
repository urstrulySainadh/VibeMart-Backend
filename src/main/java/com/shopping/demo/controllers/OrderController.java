package com.shopping.demo.controllers;


import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopping.demo.entities.User;
import com.shopping.demo.services.OrderService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
//@CrossOrigin(origins = {"https://vibe-mart-sainadhvercels-projects.vercel.app", "http://localhost:5173"}, allowCredentials = "true")
@RequestMapping("/api/orders")
public class OrderController {
	
	OrderService orderService;
	
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}
	
	/**
     * Fetches all successful orders for the authenticated user.
     * @param request HttpServletRequest containing the authenticated user details.
     * @return A ResponseEntity containing the user's role, username, and their orders.
     */
	@GetMapping
	public ResponseEntity<Map<String,Object>> getOrdersForUsers(HttpServletRequest request) {
		
		try {
			 // Retrieve the authenticated user from the request
			User authenticatedUser = (User) request.getAttribute("authenticatedUser");
			
			 // Handle unauthenticated requests
			if(authenticatedUser == null) {
				return ResponseEntity.status(401).body(Map.of("error","User Not Authenticated"));
			}
			
			 // Fetch orders for the user via the service layer
			Map<String,Object> response = orderService.getOrdersForUser(authenticatedUser);
			
			// Return the response with HTTP 200 OK
			return ResponseEntity.ok(response);
		}
		catch(Exception e) {
			// Handle cases where user details are invalid or missing and unexpected exceptions
			return ResponseEntity.status(400).body(Map.of("error",e.getMessage()));
		}
		
	}
}
