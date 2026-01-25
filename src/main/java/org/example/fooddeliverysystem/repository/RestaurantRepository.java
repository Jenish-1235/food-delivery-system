package org.example.fooddeliverysystem.repository;

import java.util.List;
import java.util.Optional;

import org.example.fooddeliverysystem.model.Restaurant;
import org.example.fooddeliverysystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {

    Optional<Restaurant> findByUser(User user);

    List<Restaurant> findByCity(String city);

    List<Restaurant> findByCityAndState(String city, String state);
}
