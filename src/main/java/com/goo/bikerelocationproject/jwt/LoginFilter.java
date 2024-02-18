package com.goo.bikerelocationproject.jwt;

import com.goo.bikerelocationproject.data.dto.auth.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {

  private static final String SPRING_SECURITY_FORM_EMAIL_KEY = "email";
  private static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
  private static final String LOGIN_REQUEST_URL = "/auth/login";
  private static final String HTTP_METHOD = "POST";
  private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
      new AntPathRequestMatcher(LOGIN_REQUEST_URL, HTTP_METHOD);

  public static final String TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";

  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;

  public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
    super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;

  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {

    String email = obtainEmail(request);
    String password = obtainPassword(request);
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        email, password, null);

    return authenticationManager.authenticate(authToken);
  }

  protected String obtainEmail(HttpServletRequest request) {
    return request.getParameter(SPRING_SECURITY_FORM_EMAIL_KEY);
  }

  protected String obtainPassword(HttpServletRequest request) {
    return request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) {

    CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
    String email = userDetails.getEmail();

    Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();

    String role = auth.getAuthority();
    String token = jwtUtil.createJwt(email, role, Duration.ofMinutes(10).toMillis());

    response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + token);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) {

    response.setStatus(401);
  }
}
