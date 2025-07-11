package com.shopping.demo.services;

import java.nio.charset.StandardCharsets;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopping.demo.dtos.LoginDTO;
import com.shopping.demo.entities.JWTToken;
import com.shopping.demo.entities.User;
import com.shopping.demo.repositories.JWTTokenRepository;
import com.shopping.demo.repositories.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {
	
	private final Key SIGNING_KEY;
	private final UserRepository userRepository;
	private final JWTTokenRepository jwtTokenRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
	// Injecting jwt.secret from properties file
	@Autowired
	public AuthService(UserRepository userRepository, JWTTokenRepository jwtTokenRepository, @Value("${jwt.secret}") String jwtSecret) {
		this.userRepository = userRepository;
		this.jwtTokenRepository = jwtTokenRepository;
		passwordEncoder = new BCryptPasswordEncoder();
		this.SIGNING_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}
	
	public User authenticate(String username, String password) {
		
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Invalid username or password"));
		if(!passwordEncoder.matches(password, user.getPassword())) {
			throw new RuntimeException("invalid username or password");
		}
		return user;
	}
	
	public String generateToken(User user) {
		String token;
		LocalDateTime currentTime = LocalDateTime.now();
		//from repository fetching the existing token
		JWTToken existingToken = jwtTokenRepository.findByUserId(user.getUserId());
		//get the time of token expairy
		if(existingToken!=null && currentTime.isBefore(existingToken.getExpiresAt())) {
			token = existingToken.getToken();
		}
		else {
			token = generateNewToken(user);
			if(existingToken != null) {
				jwtTokenRepository.delete(existingToken);
			}
			saveToken(token, user);
		}
		
		return token;
	}
	
	public String generateNewToken(User user) {
		
		String token = Jwts.builder().
				setSubject(user.getUsername()).
				claim("role", user.getRole().name()).
				setIssuedAt(new Date()).
				setExpiration(new Date(System.currentTimeMillis()+3600000)).
				signWith(SIGNING_KEY, SignatureAlgorithm.HS512).
				compact();
		return token;
	}
	
	public void saveToken(String  token, User user) {
		JWTToken jwtToken = new JWTToken(user, token, LocalDateTime.now().plusHours(1));
		jwtTokenRepository.save(jwtToken);
		
	}
	
	public void logout(User user) {
	    jwtTokenRepository.deleteByUserId(user.getUserId());
	}

	
	public boolean validateToken(String token) {
		try {
			System.err.println("Validating Token");
			
			// parse and validate the token
			Jwts.parserBuilder()
			.setSigningKey(SIGNING_KEY)
			.build()
			.parseClaimsJws(token);

			// check if the token exists in the database and is not expired\
			Optional<JWTToken> jwtToken = jwtTokenRepository.findByToken(token);
			if(jwtToken.isPresent()) {
				return jwtToken.get().getExpiresAt().isAfter(LocalDateTime.now());
			}
			return false;	
		}
		catch(Exception e) {
			System.err.println("Token Validation Failed: " +  e.getMessage());
			return false;
		}
	}
	
	public String extractUserName(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(SIGNING_KEY)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
}
