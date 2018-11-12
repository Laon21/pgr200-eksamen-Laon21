package no.kristiania.pgr200.database.core;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Database extends DaoMethods {

    Database(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Inserts a talk object into the database
     *
     * @param talk generated in server
     */
    void insertTalk(Talk talk) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO talks ( title, description ,topic) values (?, ?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, talk.getTitle());
                statement.setString(2, talk.getDescription());
                statement.setString(3, talk.getTopic());
                statement.executeUpdate();
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        talk.setId(generatedKeys.getInt(1));
                    }

                }
            } catch (SQLException e) {
                System.out.println("sql error when trying to insert talk");
            }
        } catch (SQLException e) {
            System.out.println("sql error when trying to insert talk");
        }
    }

    /**
     * Generates a talk object from the resultSet of a SQL query
     *
     * @param result ResultSet
     * @return A new talk object
     */
    private Talk mapToTalk(ResultSet result) {
        Talk talk = new Talk();
        try {
            talk.setId(result.getInt(1));
            talk.setTitle(result.getString("title"));
            talk.setDescription(result.getString("description"));
            talk.setTopic(result.getString("topic"));
        } catch (SQLException e) {
            System.out.println("failed to map to talk");
        }
        return talk;
    }

    /**
     * Returns all elements in the database as talk objects
     *
     * @return List<Talk>
     */
    public List<Talk> listAll() {
        try {
            return list("SELECT * FROM TALKS", result -> mapToTalk(result));
        } catch (SQLException e) {
            System.out.println("failed to list talk due to SQLException");
            return null;
        }
    }

    /**
     * Returns a single element from the database
     *
     * @param id element's id in the database
     * @return A new Talk object with elements values
     */
    Talk getTalk(int id) {
        try {
            return getSingleObject("select * FROM TALKS where talk_id = " + id, result -> mapToTalk(result));
        } catch (SQLException e) {
            System.out.println("Failed to get talk due to SQLException");
            return null;
        }
    }

    /**
     * Updates a element in the database with new values provided
     *
     * @param arguments Map containing new values
     */
    void updateTalk(Map<String, String> arguments) {
        String sql = checkUpdateArgs(arguments);
        try {
            updateSingleObject(sql, result -> mapToTalk(result));
            System.out.println("Update successful");
        } catch (SQLException e) {
            System.out.println("failed to update Talk due to SQLException");
        }

    }

    /**
     * Checks arguments for values and builds the SQL query
     *
     * @param arguments Map with new values for the element
     * @return String containing the SQL query
     */
    private String checkUpdateArgs(Map<String, String> arguments) {
        StringBuilder sql = new StringBuilder("update talks set ");
        try {
            arguments.keySet()
                    .forEach(key -> {
                        if (key.equalsIgnoreCase("id")) {

                        } else {
                            sql.append(key).append("='").append(arguments.get(key)).append("'").append(",");
                        }
                    });
        } finally {
            sql.deleteCharAt(sql.lastIndexOf(","));
            sql.append(" where talk_id=").append(arguments.get("id"));
        }
        return sql.toString();
    }

    /**
     * Drops all the tables from the database recreates a baseline version
     * This will make the server shut down and will need to be restarted
     * When the server connects to the database the migration script recreate the previous tables
     */
    void resetDb() {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "drop table conference, talks, days, flyway_schema_history, time_slots, tracks";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.executeUpdate();
                Flyway.configure().dataSource(dataSource).load().baseline();
            }
        } catch (SQLException e) {
            System.out.println("failed to drop tables in the database");
        }
    }
}
