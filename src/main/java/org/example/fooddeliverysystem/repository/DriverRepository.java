package org.example.fooddeliverysystem.repository;

import java.util.Optional;

import org.example.fooddeliverysystem.model.Driver;
import org.example.fooddeliverysystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {

    Optional<Driver> findByUser(User user);
}
