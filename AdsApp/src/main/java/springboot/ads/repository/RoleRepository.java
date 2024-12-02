package springboot.ads.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import springboot.ads.entities.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}