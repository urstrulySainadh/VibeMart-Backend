package com.shopping.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow frontend origins
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "https://vibe-mart-sainadhvercels-projects.vercel.app"
        ));

        // Allow credentials (cookies)
        config.setAllowCredentials(true);

        // Allow specific headers used in requests
		//config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With"));
        config.setAllowedHeaders(Arrays.asList("*"));

        // Allow HTTP methods
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Allow exposed headers (important for frontend)
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Apply CORS settings to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
