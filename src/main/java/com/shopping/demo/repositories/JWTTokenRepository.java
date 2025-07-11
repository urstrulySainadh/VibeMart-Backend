package com.shopping.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.shopping.demo.entities.JWTToken;

@Repository
public interface JWTTokenRepository extends JpaRepository<JWTToken, Integer> {
	 // Find a token by its value
	 Optional<JWTToken> findByToken(String token);
	
	// Custom query to find tokens by user ID
	@Query("SELECT  t from JWTToken t where t.user.userId = :userId")
	JWTToken findByUserId(@Param("userId") int userId);
	
	 // Custom query to delete tokens by user ID
	@Modifying
	@Transactional
	@Query("DELETE from JWTToken t where t.user.userId = :userId")
	void deleteByUserId(@Param("userId") int userId);
}
