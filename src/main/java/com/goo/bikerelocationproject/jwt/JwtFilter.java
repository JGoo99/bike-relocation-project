package com.goo.bikerelocationproject.jwt;

import com.goo.bikerelocationproject.data.dto.auth.CustomUserDetails;
import com.goo.bikerelocationproject.data.entity.CustomUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  public static final String TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";
  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String authorization = request.getHeader(TOKEN_HEADER);
    if (authorization == null || !authorization.startsWith(TOKEN_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authorization.split(" ")[1];
    if (jwtUtil.isExpired(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    CustomUser user = CustomUser.builder()
        .username(jwtUtil.getUsername(token))
        .role(jwtUtil.getRole(token))
        .password("tempPassword")
        .build();

    CustomUserDetails userDetails = new CustomUserDetails(user);
    Authentication authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    filterChain.doFilter(request, response);
  }
}
