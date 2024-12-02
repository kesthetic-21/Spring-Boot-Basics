package io.javabrains.springbootquickstart.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class TopicsController {

    @RequestMapping("/topics")
    public List<Topic> topics() {
        return Arrays.asList(
                new Topic("spring1", "Spring Boot", "Spring Framework Desc"),
                new Topic("spring2", "Spring Boot 2", "Spring Framework Desc 2"),
                new Topic("spring3", "Spring Boot 3", "Spring Framework Desc 3")
        );
    }
}
