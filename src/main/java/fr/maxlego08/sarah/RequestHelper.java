package fr.maxlego08.sarah;

import fr.maxlego08.sarah.database.Schema;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class RequestHelper {

    private final DatabaseConnection connection;
    private final Logger logger;

    public RequestHelper(DatabaseConnection connection, Logger logger) {
        this.connection = connection;
        this.logger = logger;
    }

    public void upsert(String tableName, Consumer<Schema> consumer) {
        try {
            SchemaBuilder.upsert(tableName, consumer).execute(this.connection.getConnection(), this.connection.getDatabaseConfiguration(), this.logger);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void update(String tableName, Consumer<Schema> consumer) {
        try {
            SchemaBuilder.update(tableName, consumer).execute(this.connection.getConnection(), this.connection.getDatabaseConfiguration(), this.logger);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void insert(String tableName, Consumer<Schema> consumer) {
        insert(tableName, consumer, id -> {
        });
    }

    public void insert(String tableName, Consumer<Schema> consumer, Consumer<Integer> consumerResult) {
        try {
            consumerResult.accept(SchemaBuilder.insert(tableName, consumer).execute(this.connection.getConnection(), this.connection.getDatabaseConfiguration(), this.logger));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public long count(String tableName, Consumer<Schema> consumer) {
        Schema schema = SchemaBuilder.selectCount(tableName);
        consumer.accept(schema);
        try {
            return schema.executeSelectCount(this.connection.getConnection(), this.connection.getDatabaseConfiguration(), this.logger);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0L;
    }

    public <T> List<T> select(String tableName, Class<T> clazz, Consumer<Schema> consumer) {
        Schema schema = SchemaBuilder.select(tableName);
        consumer.accept(schema);
        try {
            return schema.executeSelect(clazz, this.connection.getConnection(), this.connection.getDatabaseConfiguration(), this.logger);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void delete(String tableName, Consumer<Schema> consumer) {
        Schema schema = SchemaBuilder.delete(tableName);
        consumer.accept(schema);
        try {
            schema.execute(this.connection.getConnection(), this.connection.getDatabaseConfiguration(), this.logger);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
