package io.springboot.java.meetingscheduler.repository;

import io.springboot.java.meetingscheduler.entities.User;
import io.springboot.java.meetingscheduler.enums.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Query("SELECT user FROM User user WHERE user.role = :role")
    List<User> findByRole(@Param("role") UserRoles role);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByRole(String role);
}