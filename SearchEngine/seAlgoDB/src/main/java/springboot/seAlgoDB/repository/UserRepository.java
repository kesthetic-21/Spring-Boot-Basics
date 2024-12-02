package springboot.seAlgoDB.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot.seAlgoDB.entities.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Searching by title or content using 'LIKE'
    @Query("SELECT user FROM User user WHERE LOWER(user.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(user.skills) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> searchByKeyword(@Param("query") String query);

    // Paginated search
    @Query("SELECT user FROM User user WHERE LOWER(user.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(user.skills) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> searchByKeywordPaginated(@Param("query") String query, Pageable pageable);
    
}
