package com.sample.aws.dynamo.basic_crud.config;

import com.sample.aws.dynamo.basic_crud.entity.Product;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

@Configuration
public class DynamoDbTableInitializer {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbClient standardClient;

    public DynamoDbTableInitializer(DynamoDbEnhancedClient enhancedClient, DynamoDbClient standardClient) {
        this.enhancedClient = enhancedClient;
        this.standardClient = standardClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createTable() {
        var table = enhancedClient.table("Product", TableSchema.fromBean(Product.class));

        try {
            table.createTable();
            System.out.println("DynamoDB Table 'Product' created successfully.");
        } catch (ResourceInUseException e) {
            System.out.println("DynamoDB Table 'Product' already exists.");
        }

        // DEBUG: List all tables to prove we are connected to the right DB
        System.out.println("--- EXISTING TABLES ---");
        standardClient.listTables().tableNames().forEach(System.out::println);
        System.out.println("-----------------------");
    }
}