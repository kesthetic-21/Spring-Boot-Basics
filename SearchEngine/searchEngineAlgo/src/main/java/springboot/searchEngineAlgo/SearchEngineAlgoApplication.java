package springboot.searchEngineAlgo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class SearchEngineAlgoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchEngineAlgoApplication.class, args);
	}

}
