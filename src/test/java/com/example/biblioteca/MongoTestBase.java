package com.example.biblioteca;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class MongoTestBase {

    @Container
    static final MongoDBContainer mongo =
            new MongoDBContainer("mongo:7.0");

    static {
        mongo.start();
    }

    @DynamicPropertySource
    static void configureMongo(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.data.mongodb.uri",
                mongo::getReplicaSetUrl
        );
    }
}