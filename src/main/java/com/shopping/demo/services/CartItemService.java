package com.shopping.demo.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shopping.demo.entities.CartItem;
import com.shopping.demo.entities.Product;
import com.shopping.demo.entities.ProductImage;
import com.shopping.demo.entities.User;
import com.shopping.demo.repositories.CartItemRepository;
import com.shopping.demo.repositories.CartRepository;
import com.shopping.demo.repositories.ProductImageRepository;
import com.shopping.demo.repositories.ProductRepository;
import com.shopping.demo.repositories.UserRepository;

@Service
public class CartItemService {
	CartRepository cartItemRepository;
	ProductRepository productRepository;
	ProductImageRepository productImageRepository;
//	CartItemRepository cartItemRepository;
	UserRepository userRepository;
	public CartItemService(CartRepository cartItemRepository,UserRepository userRepository, ProductRepository productRepository,ProductImageRepository productImageRepository) {
		this.cartItemRepository = cartItemRepository;
		this.userRepository = userRepository;
		this.productRepository = productRepository;
		this.productImageRepository = productImageRepository;
	}
	
	// Get the total cart item count for a user
	public int getCartItemCount(int userId) {
		return cartItemRepository.countTotalItems(userId);
	}
	
	// Add an item to the cart
	public void addToCart(int userId, int productId, int quantity) {
		
		User user = userRepository.findById(userId).orElse(null);
		Product product = productRepository.findById(productId).orElse(null);
		// Fetch cart item for this userId and productId
		Optional<CartItem> existingItem = cartItemRepository.findByUserAndProduct(userId,productId);
		if(existingItem.isPresent()) {
			CartItem cartItem = existingItem.get();
			cartItem.setQuantity(quantity);
			cartItemRepository.save(cartItem);
		}
		else {
			CartItem cartItem = new CartItem(user, product, quantity);
			cartItemRepository.save(cartItem);
		}
		
	}
	
	// Get Cart Items for a User
	public Map<String,Object> getCartItems(int userId) {
		
		// Fetch the cart items for the user with product details
		List<CartItem>  cartItems = cartItemRepository.findCartItemsWithProductDetails(userId);
		// Create a response map to hold the cart details
		Map<String,Object> response = new HashMap<>();
		
		User user = userRepository.findById(userId).orElse(null);
		response.put("username", user.getUsername());
		response.put("role", user.getRole());
		
		// List to hold the all individual product details
		List<Map<String, Object>> products  = new ArrayList<>();
		int overAllTotalPrice = 0;
		
		for(CartItem cartItem: cartItems) {
			// Map to hold the each individual product details
			Map<String,Object> productDetails = new HashMap<>();
			
			// Get product details
			Product product = cartItem.getProduct();
			
			// Fetch product images from the ProductImageRepository
			List<ProductImage> productImages = productImageRepository.findByProduct_ProductId(product.getProductId());
			
			String imageUrl = null;
			if(productImages != null && !productImages.isEmpty()) {
				// If there are images, get the first image's URL
				imageUrl = productImages.get(0).getImageUrl();
			}
			else {
				imageUrl = "image not found";
			}
			productDetails.put("product_id", product.getProductId());
			productDetails.put("imageUrl", imageUrl);
			productDetails.put("name", product.getName());
			productDetails.put("description", product.getDescription());
			productDetails.put("price_per_unit", product.getPrice());
			productDetails.put("quantity", cartItem.getQuantity());
			double overallprice = cartItem.getQuantity()*product.getPrice().doubleValue();
			productDetails.put("total_price", overallprice);
			
			// Add the product details to the products list
			products.add(productDetails);
			
			// Add to the overall total price
			overAllTotalPrice += overallprice;
			
		}
		// Prepare the final cart response
		Map<String, Object> cart = new HashMap<>();
		cart.put("products", products);
		cart.put("overall_tota_price", overAllTotalPrice);
		
		// Add the cart details to the response
		response.put("cart", cart);
		return response;
	}
	
	// Update cart item quantity
	public void updateCartItemQuantity(int userId, int productId, int quantity) {
		
		User user = userRepository.findById(userId)
				.orElseThrow(()-> new IllegalArgumentException("User not found"));
		
		Product product = productRepository.findById(productId)
				.orElseThrow(()-> new IllegalArgumentException("Product not found"));
		
		// Fetch cart item for this userId and productId
		Optional<CartItem> existingItem = cartItemRepository.findByUserAndProduct(userId,productId);
		CartItem cartItem = existingItem.get();
		if(existingItem.isPresent()) {
			if(quantity == 0) {
				deleteCartItem(userId,productId);
			} else {
				cartItem.setQuantity(quantity);
				cartItemRepository.save(cartItem);
			}
		}
	}
	 
	// Delete cart item
	public void deleteCartItem(int userId, int productId) {
		User user = userRepository.findById(userId)
				.orElseThrow(()-> new IllegalArgumentException("User not found"));
		
		Product product = productRepository.findById(productId)
				.orElseThrow(()-> new IllegalArgumentException("Product not found"));
		cartItemRepository.deleteCartItem(userId, productId);
	}
}
