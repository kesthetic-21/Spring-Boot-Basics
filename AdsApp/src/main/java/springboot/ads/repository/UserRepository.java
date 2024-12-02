package springboot.ads.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot.ads.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.preferences p WHERE p = :preference")
    List<User> findByPreferences(@Param("preference") String preference);
}