package org.example.fooddeliverysystem.repository;

import java.util.List;
import java.util.Optional;

import org.example.fooddeliverysystem.model.Driver;
import org.example.fooddeliverysystem.model.Order;
import org.example.fooddeliverysystem.model.Restaurant;
import org.example.fooddeliverysystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUser(User user);

    List<Order> findByRestaurant(Restaurant restaurant);

    List<Order> findByDriver(Driver driver);

    Optional<Order> findByOrderNumber(String orderNumber);
}
