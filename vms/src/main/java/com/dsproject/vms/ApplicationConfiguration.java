package com.dsproject.vms;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@AutoConfigureAfter(EmbeddedMongoAutoConfiguration.class)
public class ApplicationConfiguration extends AbstractMongoClientConfiguration {

    /*
    @Value(value = "${MONGO_HOST}")
    private String mongoHost;

    @Value(value = "${MONGO_ROOT_USERNAME}")
    private String mongoUser;

    @Value(value = "${MONGO_ROOT_PASSWORD}")
    private String mongoPass;

    @Value(value = "${MONGO_PORT:27017}")
    private String mongoPort;

    @Value(value = "${MONGO_DBNAME}")
    private String mongoDatabase;
    */
    public ApplicationConfiguration() {
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        /*
        String s = String.format("mongodb://%s:%s/%s",
                mongoHost, mongoPort, mongoDatabase);
         */
        String s = String.format("mongodb://localhost:27017/vms");
        return MongoClients.create(s);
    }

    @Override
    protected String getDatabaseName() {
        return "mongoDatabase";
    }
}


