package com.sample.aws.dynamo.basic_crud.repository;

import com.sample.aws.dynamo.basic_crud.entity.Product;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Repository
public class ProductRepository extends CrudRepository<Product> {

    public ProductRepository(DynamoDbEnhancedClient enhancedClient) {
        super(enhancedClient, "Product", Product.class);
    }
}