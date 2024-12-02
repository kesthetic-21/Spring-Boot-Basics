package springboot.searchEngine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.searchEngine.entities.Book;
import springboot.searchEngine.services.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/engine")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/addBook")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBook (@RequestParam String query){
        List<Book> books = bookService.search(query);

        return ResponseEntity.status(200).body(books);
    }

    @GetMapping("/searchPaginated")
    public ResponseEntity<List<Book>> searchBookPaginated (@RequestParam String query, @RequestParam int pages, @RequestParam int size){
        Page<Book> books = bookService.searchThroughPages(query, pages, size);

        return ResponseEntity.status(200).body(books.getContent());
    }
}
