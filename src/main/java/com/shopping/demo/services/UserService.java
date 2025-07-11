package com.shopping.demo.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopping.demo.entities.User;
import com.shopping.demo.repositories.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = new BCryptPasswordEncoder();
	}

	public User userRegister(User user) {
		
		if(userRepository.findByUsername(user.getUsername()).isPresent()) {
		//throw exception
			throw new RuntimeException("Username is already taken.");
		}
		if(userRepository.findByUsername(user.getEmail()).isPresent()) {
		//throw exception
			throw new RuntimeException("Email is already registered.");
		}
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}
}
