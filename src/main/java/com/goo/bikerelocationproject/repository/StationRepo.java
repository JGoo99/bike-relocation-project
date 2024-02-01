package com.goo.bikerelocationproject.repository;

import com.goo.bikerelocationproject.data.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepo extends JpaRepository<Station, Long> {

}
