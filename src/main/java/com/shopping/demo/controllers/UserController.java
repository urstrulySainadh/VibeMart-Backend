package com.shopping.demo.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopping.demo.entities.User;
import com.shopping.demo.services.UserService;

@RestController
//@CrossOrigin(origins = {"https://vibe-mart-sainadhvercels-projects.vercel.app", "http://localhost:5173"}, allowCredentials = "true")
@RequestMapping("/api/users")
public class UserController {

	private UserService userService;
	
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody User user){
		try {
			User registeredUser = userService.userRegister(user);
			return ResponseEntity.ok(Map.of("message","user registered successfully", "user",registeredUser));
		}
		catch(Exception e) {
			return ResponseEntity.badRequest().body(Map.of("ERROR", e.getMessage()));
		}
	}
	
	
}
