package com.goo.bikerelocationproject.service.impl;

import static com.goo.bikerelocationproject.type.ErrorCode.NOT_FOUND_USER;

import com.goo.bikerelocationproject.data.dto.auth.CustomUserDetails;
import com.goo.bikerelocationproject.data.entity.CustomUser;
import com.goo.bikerelocationproject.exception.StationException;
import com.goo.bikerelocationproject.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepo userRepo;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    CustomUser user = userRepo.findByEmail(email)
        .orElseThrow(() -> new StationException(NOT_FOUND_USER));

    return new CustomUserDetails(user);
  }
}
