package org.example.fooddeliverysystem.repository;

import java.util.Optional;

import org.example.fooddeliverysystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Override
    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNo(String phoneNo);

    Optional<User> findByEmailAndEnabledTrue(String email);

    Optional<User> findByPhoneNoAndEnabledTrue(String phoneNo);

    boolean existsByEmail(String email);

    boolean existsByPhoneNo(String phoneNo);

    @Query("SELECT u FROM User u WHERE u.email = :email OR u.phoneNo = :phoneNo")
    Optional<User> findByEmailOrPhoneNo(@Param("email") String email, @Param("phoneNo") String phoneNo);

    @Query("SELECT u FROM User u WHERE (u.email = :email OR u.phoneNo = :phoneNo) AND u.enabled = true")
    Optional<User> findByEmailOrPhoneNoAndEnabledTrue(@Param("email") String email, @Param("phoneNo") String phoneNo);
}