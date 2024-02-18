package com.goo.bikerelocationproject.config;

import com.goo.bikerelocationproject.jwt.JwtFilter;
import com.goo.bikerelocationproject.jwt.JwtUtil;
import com.goo.bikerelocationproject.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final JwtUtil jwtUtil;

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {

    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {

    return configuration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .csrf((auth) -> auth.disable())
        .formLogin((auth) -> auth.disable())
        .httpBasic((auth) -> auth.disable());

    http
        .cors((cors) -> cors
            .configurationSource(new CorsConfigurationSource() {
              @Override
              public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOrigins(
                    Collections.singletonList("http://localhost:3000"));
                corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
                corsConfiguration.setAllowCredentials(true);
                corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
                corsConfiguration.setMaxAge(3600L);
                corsConfiguration.setExposedHeaders(Collections.singletonList("Authorization"));

                return corsConfiguration;
              }
            }));

    http
        .authorizeHttpRequests((auth) -> auth
            .requestMatchers("/", "/api/open-api-station", "/api/open-api-parking").permitAll()
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/api/station/**").hasRole("USER")
            .anyRequest().authenticated());

    http
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterAfter(
            new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
            LogoutFilter.class)
        .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

    return http.build();
  }
}
