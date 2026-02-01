package com.sample.aws.dynamo.basic_crud.repository;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.stream.Collectors;

public class CrudRepository <T> {

    protected DynamoDbTable<T> transactionTable;

    public CrudRepository(DynamoDbEnhancedClient enhancedClient, String tableName, Class<T> clazz) {
        this.transactionTable = enhancedClient.table(tableName, TableSchema.fromBean(clazz));
    }

    public T findById(String id) {
        return JdbcListeners.wrap(() -> {
            Key key = Key.builder().partitionValue(id).build();
            return transactionTable.getItem(key);
        });

    }

    public T save(T entity) {
        return JdbcListeners.wrap(() -> {
            transactionTable.putItem(entity);
            return entity;
        });
    }

    public void delete(String id) {
        JdbcListeners.wrap(() -> {
            Key key = Key.builder().partitionValue(id).build();
            transactionTable.deleteItem(key);
            return null;
        });
    }

    public List<T> getAll() {
        return JdbcListeners.wrap(() -> transactionTable.scan()
                .items()
                .stream()
                .collect(Collectors.toList()));
    }


}
