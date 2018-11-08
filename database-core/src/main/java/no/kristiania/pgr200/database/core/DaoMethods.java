package no.kristiania.pgr200.database.core;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public abstract class DaoMethods {

    public final DataSource dataSource;

    public DaoMethods(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T getSingleObject(String sql, ResultSetMapper<T> mapper) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return mapper.mapResultSet(result);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
    }

    public <T> void updateSingleObject(String sql, ResultSetMapper<T> mapper) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();

            }
        }
    }


    protected <T> List<T> list(String sql, ResultSetMapper<T> mapper) throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                try(ResultSet result = statement.executeQuery()){
                    List<T> resultList = new ArrayList<>();
                    while (result.next()){
                        resultList.add(mapper.mapResultSet(result));
                    }
                    return resultList;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

}
