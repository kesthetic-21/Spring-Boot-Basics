package springboot.seAlgoDB.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import springboot.seAlgoDB.entities.User;
import springboot.seAlgoDB.models.SearchResult;

import java.util.List;

@Service
public class EngineService {

    @Autowired
    private UserService userService;

    @Autowired
    private SearchService searchService;

    public List<SearchResult> performCombinedSearch(String query) {
        List<User> users = userService.search(query);

        List<String> skills = users.stream().map(User::getSkills).toList();
        List<String> usernames = users.stream().map(User::getUsername).toList();

        return searchService.performSearch(skills, usernames, query);
    }

    public Page<SearchResult> paginatedSearch(String query, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        Page<User> usersPage = userService.searchThroughPages(query, page, size);

        List<User> users = usersPage.getContent();
        List<String> skills = users.stream().map(User::getSkills).toList();
        List<String> usernames = users.stream().map(User::getUsername).toList();

        List<SearchResult> results = searchService.performSearch(skills, usernames, query);

        return new PageImpl<>(results, pageable, usersPage.getTotalElements());
    }

}