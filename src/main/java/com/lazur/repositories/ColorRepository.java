package com.lazur.repositories;

import com.lazur.entities.specific.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long>{

    Color findByName(String colorName);
}
