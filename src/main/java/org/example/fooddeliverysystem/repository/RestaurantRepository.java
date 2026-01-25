package org.example.fooddeliverysystem.repository;

import java.util.List;
import java.util.Optional;

import org.example.fooddeliverysystem.model.Restaurant;
import org.example.fooddeliverysystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {

    Optional<Restaurant> findByUser(User user);

    List<Restaurant> findByCity(String city);

    List<Restaurant> findByCityAndState(String city, String state);
    
    @Query("SELECT r FROM Restaurant r WHERE r.city = :city AND r.isOpen = true")
    List<Restaurant> findByCityAndIsOpenTrue(@Param("city") String city);
    
    @Query("SELECT r FROM Restaurant r WHERE r.city = :city AND r.state = :state AND r.isOpen = true")
    List<Restaurant> findByCityAndStateAndIsOpenTrue(@Param("city") String city, @Param("state") String state);
    
    Page<Restaurant> findByCity(String city, Pageable pageable);
    
    Page<Restaurant> findByCityAndState(String city, String state, Pageable pageable);
    
    Page<Restaurant> findByIsOpenTrue(Pageable pageable);
}
