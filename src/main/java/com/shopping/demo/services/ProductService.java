package com.shopping.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopping.demo.entities.Category;
import com.shopping.demo.entities.Product;
import com.shopping.demo.entities.ProductImage;
import com.shopping.demo.repositories.CategoryRepository;
import com.shopping.demo.repositories.ProductImageRepository;
import com.shopping.demo.repositories.ProductRepository;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ProductImageRepository productImageRepository;

	@Autowired
	private CategoryRepository categoryRepository;
	
	public List<Product> getProductsByCategory(String categoryName){
		if(categoryName != null && !categoryName.isEmpty()) {
			Optional<Category> categoryOpt = categoryRepository.findByCategoryName(categoryName);
			if(categoryOpt.isPresent()) {
				Category category = categoryOpt.get();
				return productRepository.findByCategory_CategoryId(category.getCategoryId());
			} else {
				throw new RuntimeException("Category Not Found");
			}
		} else {
			return productRepository.findAll();
		}
	}
	
	public List<String> getProductImages(Integer productId){
		List<ProductImage> productImages = productImageRepository.findByProduct_ProductId(productId);
		List<String> imageUrls = new ArrayList<>();
		
		for(ProductImage image: productImages) {
			imageUrls.add(image.getImageUrl());
		}
		
		return imageUrls;
	}
	
}
