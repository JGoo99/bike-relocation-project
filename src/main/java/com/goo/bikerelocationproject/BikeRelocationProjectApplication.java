package com.goo.bikerelocationproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BikeRelocationProjectApplication {

  public static void main(String[] args) {
    SpringApplication.run(BikeRelocationProjectApplication.class, args);
  }

}
