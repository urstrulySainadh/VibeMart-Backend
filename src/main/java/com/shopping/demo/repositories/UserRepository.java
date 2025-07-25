package com.shopping.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopping.demo.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	
}
