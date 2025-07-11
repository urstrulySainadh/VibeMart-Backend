package com.shopping.demo.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopping.demo.entities.User;
import com.shopping.demo.repositories.UserRepository;
import com.shopping.demo.services.CartItemService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
//@CrossOrigin(origins = {"https://vibe-mart-sainadhvercels-projects.vercel.app", "http://localhost:5173"}, allowCredentials = "true")
@RequestMapping("/api/cart")
public class CartController {
	
	UserRepository userRepository;
	CartItemService cartItemService;
	
	public CartController(UserRepository userRepository, CartItemService cartItemService) {
		this.userRepository = userRepository;
		this.cartItemService = cartItemService;
	}
	
	// Fetch userId from username coming from the filter and get cart item count
	@GetMapping("items/count")
	public ResponseEntity<Integer> getCartCount(@RequestParam String username){
		int count = 0;		
		 // Fetch user by username to get the userId
		Optional<User> opuser= userRepository.findByUsername(username);
		if(opuser != null) {
			User user = opuser.get();
			 // Call the service to get the total cart item count
			count = cartItemService.getCartItemCount(user.getUserId());
		}
		return ResponseEntity.ok(count);
	}
	
	// Fetch all cart items for the user (based on username)
	@GetMapping("/items")
	public ResponseEntity<Map<String, Object>> getCartItems(HttpServletRequest request){
		// Fetch user by username to get the userId
		User user = (User) request.getAttribute("authenticatedUser");
		 // Call the service to get cart items for the user
		Map<String, Object> response = cartItemService.getCartItems(user.getUserId());
		return ResponseEntity.ok(response);
	}
	
	// Add an item to the cart
	@PostMapping("/add")
	public ResponseEntity<Void> addToCart(@RequestBody Map<String, Object> request){
		
		String username = (String) request.get("username");
		int productId = (int) request.get("productId");
	    // Fetch the user using username
		User user = userRepository.findByUsername(username).orElse(null);
		
		// Handle quantity: Default to 1 if not provided
        int quantity = request.containsKey("quantity") ? (int) request.get("quantity") : 1;
        // Add the product to the cart
		cartItemService.addToCart(user.getUserId(), productId, quantity);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	// UPdate cart item quantity
	@PutMapping("/update")
	public ResponseEntity<Void> updateCartItemQuantity(@RequestBody Map<String, Object> request){
		String username = (String) request.get("username");
		int productId = (int) request.get("productId");
		int quantity = (int) request.get("quantity");
		
		// Fetch the user using username
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("user not found with username: " + username));
		
		// Update the cart item quantity
		cartItemService.updateCartItemQuantity(user.getUserId(), productId, quantity);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	//Delete cart item
	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteCartItem(@RequestBody Map<String,Object> request){
		String username = (String) request.get("username");
		int productId = (int) request.get("productId");
		
		// Fetch the user using username
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("user not found with username: " + username));
		
		// Delete the cart item
        cartItemService.deleteCartItem(user.getUserId(), productId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
