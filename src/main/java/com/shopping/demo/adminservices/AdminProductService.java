package com.shopping.demo.adminservices;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.shopping.demo.entities.Category;
import com.shopping.demo.entities.Product;
import com.shopping.demo.entities.ProductImage;
import com.shopping.demo.repositories.CategoryRepository;
import com.shopping.demo.repositories.ProductImageRepository;
import com.shopping.demo.repositories.ProductRepository;

@Service
public class AdminProductService {
	
	private final ProductRepository productRepository;
	private final ProductImageRepository productImageRepository;
	private final CategoryRepository categoryRepository;
	
	public AdminProductService(ProductRepository productRepository,ProductImageRepository productImageRepository,CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.productImageRepository = productImageRepository;
		this.categoryRepository = categoryRepository;
	}
	
	public Product addProductWithImage(String name, String description, Double price, Integer stock, Integer categoryId, String imageUrl) {
	
	// Validate the category
	Category category = categoryRepository.findById(categoryId).orElse(null);
	if(category == null) {
		throw new IllegalArgumentException("Invalid categoryID");
	}
	
	// Create and save the product
	Product product = new Product(name, description, BigDecimal.valueOf(price), stock, category, LocalDateTime.now(), LocalDateTime.now());
	Product savedProduct = productRepository.save(product);
	
	    // Create and save the product image
		if(imageUrl != null && !imageUrl.isEmpty()) {
			ProductImage productImage = new ProductImage();
			productImage.setImageUrl(imageUrl);
			productImage.setProduct(savedProduct);
			
			productImageRepository.save(productImage);
		}
		else {
			throw new IllegalArgumentException("Product ImageURL cannot be empty");
		}
	
	 return savedProduct;
	}
	
	public void deleteProduct(Integer productId) {
		// Check if the product exists
		Product product = productRepository.findById(productId).orElse(null);
		if(product == null) {
			throw new IllegalArgumentException("Product Doesn't Exist");
		}
		
		// Delete the product
		productRepository.delete(product);
		
		// Delete associated product images
		productImageRepository.deleteById(productId);
	}
	
}
