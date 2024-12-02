package springboot.searchEngine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import springboot.searchEngine.entities.Book;
import springboot.searchEngine.repository.BookRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book saveBook(Book book){
        return bookRepository.save(book);
    }

    public List<Book> search(String query){
        return bookRepository.searchByKeyword(query);
    }

    public Page<Book> searchThroughPages (String query, int page, int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        return bookRepository.searchByKeywordPaginated(query, pageable);
    }

    public Book updateBook(UUID id, Book article) {

        Optional<Book> existingBook = bookRepository.findById(id);

        if (existingBook.isPresent()) {

            Book book = existingBook.get();

            book.setTitle(article.getTitle());
            book.setAuthor(article.getAuthor());
            book.setContent(article.getContent());
            book.setPublicationDate(article.getPublicationDate());
            book.setCategory(article.getCategory());
            book.setKeywords(article.getKeywords());
            return bookRepository.save(book);
        } else {
            return null;
        }
    }

    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
    }
}
