package no.kristiania.pgr200.database.core;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.List;
import java.util.Properties;

public class Database extends DaoMethods {



    public Database(DataSource dataSource) {
        super(dataSource);
    }

    public String insertTalk(Talk talk) throws SQLException {
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
                        return talk.getTitle() + "inserted with id = " + talk.getId();
                    }
                    return "No id";
                }
            }
        }
    }

    private Talk mapToTalk(ResultSet result) throws SQLException {
        Talk talk = new Talk();
        talk.setId(result.getInt(1));
        talk.setTitle(result.getString("title"));
        talk.setDescription(result.getString("description"));
        talk.setTopic(result.getString("topic"));
        return talk;
    }

    public List<Talk> listAll() throws SQLException {
        return list("SELECT * FROM TALKS", result -> mapToTalk(result));
    }

    public Talk getTalk(int id) throws SQLException {
        return getSingleObject("select * FROM TALKS where talk_id = " + id, result -> mapToTalk(result));
    }



}
