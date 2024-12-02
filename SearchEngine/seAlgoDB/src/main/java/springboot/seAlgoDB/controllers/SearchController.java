package springboot.seAlgoDB.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.seAlgoDB.entities.User;
import springboot.seAlgoDB.models.SearchResult;
import springboot.seAlgoDB.services.EngineService;
import springboot.seAlgoDB.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private EngineService engineService;
    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public ResponseEntity<User> addUser (@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/engine")
    public List<SearchResult> search(@RequestParam String query) {
        return engineService.performCombinedSearch(query);
    }

    @GetMapping("/paginated")
    public Page<SearchResult> searchPaginated(@RequestParam String query, @RequestParam int pages, @RequestParam int size) {
        return engineService.paginatedSearch(query, pages, size);
    }
}
