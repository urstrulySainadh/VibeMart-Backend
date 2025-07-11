package com.shopping.demo.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.shopping.demo.dtos.LoginDTO;
import com.shopping.demo.entities.User;
import com.shopping.demo.services.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
//@CrossOrigin(origins = {"https://vibe-mart-sainadhvercels-projects.vercel.app", "http://localhost:5173"}, allowCredentials = "true")
@RequestMapping("api/auth")
public class AuthController {
	
	AuthService authService;
	public AuthController(AuthService authService) {
		// TODO Auto-generated constructor stub
		this.authService = authService;
	}
	
	// Injects the 'app.cookie.secure' property from application.properties
    @Value("${app.cookie.secure}")
    private boolean isCookieSecure;
	
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginDTO usercred, HttpServletResponse response ) {
		try 
		{	User user= authService.authenticate(usercred.getUsername(), usercred.getPassword());
			String token = authService.generateToken(user);
			
			
			 // Use Spring's modern ResponseCookie builder for a clean and robust solution
            ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from("authToken", token)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(3600); // 1 hour

            // Dynamically set Secure and SameSite attributes based on the environment
            if (isCookieSecure) {
                // Production settings for HTTPS
                cookieBuilder.secure(true);
                cookieBuilder.sameSite("None"); // Required for cross-site requests with HTTPS
            } else {
                // Development settings for HTTP
                cookieBuilder.secure(false);
                cookieBuilder.sameSite("Lax"); // Standard for local HTTP development
            }

            ResponseCookie cookie = cookieBuilder.build();

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "LoginSuccess");
            responseBody.put("role", user.getRole().name());
            responseBody.put("username", user.getUsername());

            // Add the cookie to the response headers and send the body
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(responseBody);
			
		}
		
		catch(Exception e) 
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
		}
		
	}
	
	 @PostMapping("/logout")
	 public ResponseEntity<Map<String, String>> logout(HttpServletRequest request,HttpServletResponse response) {
	        try {
	        	User user=(User) request.getAttribute("authenticatedUser");
	            authService.logout(user);
	            ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from("authToken", "") // Empty value
	                    .httpOnly(true)
	                    .path("/")
	                    .maxAge(0); // Expire immediately

	            // The logout cookie must match the login cookie's security attributes to work correctly
	            if (isCookieSecure) {
	                cookieBuilder.secure(true);
	                cookieBuilder.sameSite("None");
	            } else {
	                cookieBuilder.secure(false);
	                cookieBuilder.sameSite("Lax");
	            }
	            
	            ResponseCookie cookie = cookieBuilder.build();

	            Map<String, String> responseBody = new HashMap<>();
	            responseBody.put("message", "Logout successful");
	            
	            return ResponseEntity.ok()
	                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
	                    .body(responseBody);
	        } catch (RuntimeException e) {
	            Map<String, String> errorResponse = new HashMap<>();
	            errorResponse.put("message", "Logout failed");
	            return ResponseEntity.status(500).body(errorResponse);
	        }
	    }
}
