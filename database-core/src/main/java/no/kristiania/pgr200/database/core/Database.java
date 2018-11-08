package no.kristiania.pgr200.database.core;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public void updateTalk(int id, List<String> args) throws SQLException {
        String sql = checkUpdateArgs(id, args);
        updateSingleObject(sql, result -> mapToTalk(result));
        System.out.println("Updated values of talk " + id);
    }

    private String checkUpdateArgs(int id, List<String> args) {
        StringBuilder sql = new StringBuilder("update talks set ");
        ArrayList<String> sqlArgs = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            if (args.get(i).toLowerCase().startsWith("-ti")) {
                sqlArgs.add("title='" + args.get(++i) + "'");
            }
            if (args.get(i).toLowerCase().startsWith("-de")) {
                sqlArgs.add("description='" + args.get(++i) + "'");

            }
            if (args.get(i).toLowerCase().startsWith("-to")) {
                sqlArgs.add("topic='" + args.get(++i) + "'");
            }
        }
        if (!sqlArgs.isEmpty()) {
            try{
            for (int y = 0; y < sqlArgs.size(); y++) {
                sql.append(sqlArgs.get(y));
                if (++y < sqlArgs.size()) {
                    sql.append(", ");
                }
            }
            }
            finally {
                sql.append(" where talk_id=").append(id);
                return sql.toString();
            }

        }

        return sql.toString();
    }


}
