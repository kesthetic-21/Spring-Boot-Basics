package springboot.searchEngine.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot.searchEngine.entities.Book;

import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

    // Searching by title or content using 'LIKE'
    @Query("SELECT book FROM Book book WHERE LOWER(book.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(book.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Book> searchByKeyword(@Param("query") String query);

    // Paginated search
    @Query("SELECT book FROM Book book WHERE LOWER(book.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(book.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Book> searchByKeywordPaginated(@Param("query") String query, Pageable pageable);
    
}