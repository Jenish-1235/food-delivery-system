package org.example.fooddeliverysystem.repository;

import java.util.List;

import org.example.fooddeliverysystem.model.FoodItem;
import org.example.fooddeliverysystem.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, String> {

    List<FoodItem> findByRestaurant(Restaurant restaurant);

    List<FoodItem> findByRestaurantAndIsDeletedFalse(Restaurant restaurant);

    List<FoodItem> findByRestaurantAndIsAvailableTrueAndIsDeletedFalse(Restaurant restaurant);
}
