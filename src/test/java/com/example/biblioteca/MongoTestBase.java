package com.example.biblioteca;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

/**
 * Classe base para todos os testes que usam TestContainers + MongoDB.
 *
 * O @DynamicPropertySource garante que a URI do container seja
 * injetada no Spring antes de qualquer teste rodar — sem isso,
 * o Spring tenta conectar na URI estática do application-test.properties
 * e falha mesmo com o container rodando.
 */
public abstract class MongoTestBase {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
}