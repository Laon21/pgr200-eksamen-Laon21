package no.kristiania.pgr200.database.core;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Database extends DaoMethods {


    public Database(DataSource dataSource) {
        super(dataSource);
    }

    public String insertTalk(Talk talk)  {
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
                        return talk.getTitle() + "inserted with talk_id = " + talk.getId();
                    }
                    return "No id";
                }
            } catch (SQLException e) {
                return "sql error when trying to insert talk";
            }
        } catch (SQLException e) {
            return "sql error when trying to insert talk";
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

    public void updateTalk(Map<String, String> arguments) throws SQLException {
        String sql = checkUpdateArgs( arguments);
        updateSingleObject(sql, result -> mapToTalk(result));
        System.out.println("Updated values of talk " + arguments.get("id"));
    }

    private String checkUpdateArgs(Map<String, String> arguments) {
        StringBuilder sql = new StringBuilder("update talks set ");
        ArrayList<String> sqlArgs = new ArrayList<>();
        for (int i = 0; i < arguments.size(); i++) {
            if (arguments.containsKey("title")) {
                sqlArgs.add("title='" + arguments.get("title") + "'");
            }
            if (arguments.containsKey("description")) {
                sqlArgs.add("description='" + arguments.get("description") + "'");

            }
            if (arguments.containsKey("topic")) {
                sqlArgs.add("topic='" + arguments.get("TOPIC") + "'");
            }
        }
        if (!arguments.isEmpty()) {
            try{
            for (int y = 0; y < sqlArgs.size(); y++) {
                sql.append(sqlArgs.get(y));
                if (++y < sqlArgs.size()) {
                    sql.deleteCharAt(sql.lastIndexOf(","));
                }
            }
            }
            finally {
                sql.append(" where talk_id=").append(arguments.get("id"));
                return sql.toString();
            }

        }

        return sql.toString();
    }


}
