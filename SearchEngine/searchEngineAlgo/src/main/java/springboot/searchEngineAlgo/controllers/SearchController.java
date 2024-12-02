package springboot.searchEngineAlgo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot.searchEngineAlgo.models.SearchRequest;
import springboot.searchEngineAlgo.models.SearchResult;
import springboot.searchEngineAlgo.services.SearchService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    public ResponseEntity<List<SearchResult>> search(@RequestBody SearchRequest searchRequest) {
        List<SearchResult> results = searchService.performSearch(searchRequest.getDocuments(), searchRequest.getQuery());
        return ResponseEntity.ok(results);
    }
}