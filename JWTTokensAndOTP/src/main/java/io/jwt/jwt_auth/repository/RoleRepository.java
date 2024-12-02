package io.jwt.jwt_auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.jwt.jwt_auth.entities.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
