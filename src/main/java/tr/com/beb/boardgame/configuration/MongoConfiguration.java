package tr.com.beb.boardgame.configuration;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "tr.com.beb.*")
public class MongoConfiguration {
    
}
