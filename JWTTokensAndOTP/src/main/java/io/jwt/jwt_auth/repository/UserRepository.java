package io.jwt.jwt_auth.repository;

import io.jwt.jwt_auth.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Boolean existsByEmail(String email);
}