package com.goo.bikerelocationproject.controller;

import com.goo.bikerelocationproject.data.dto.auth.JoinDto;
import com.goo.bikerelocationproject.data.dto.auth.UserInfoDto;
import com.goo.bikerelocationproject.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final AuthServiceImpl authService;
  private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

  @PostMapping("/join")
  public ResponseEntity<UserInfoDto> join(@RequestBody JoinDto joinDto) {

    LOGGER.info("[join]: {}", joinDto.toString());

    UserInfoDto userInfo = authService.join(joinDto);

    return ResponseEntity.ok(userInfo);
  }
}
