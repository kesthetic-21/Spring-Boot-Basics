package springboot.seAlgoDB.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import springboot.seAlgoDB.entities.User;
import springboot.seAlgoDB.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public List<User> search(String query){
        return userRepository.searchByKeyword(query);
    }

    public Page<User> searchThroughPages (String query, int page, int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        return userRepository.searchByKeywordPaginated(query, pageable);
    }
}
