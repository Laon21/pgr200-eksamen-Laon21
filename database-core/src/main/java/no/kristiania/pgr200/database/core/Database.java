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
    private String dbUrl, dbPassword, dbUserName, dbDatabase, dbHostNameInCertificate;
    private InputStream input;
    private OutputStream output;
    private boolean dbEncrypt, dbTrustServerCertificate;
    private int dbLoginTimeout;


    public Database(DataSource datasource) {
        super(datasource);
    }

    public String insertTalk(Talk talk) throws SQLException {
        try(Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO talks ( title, description ,topic) values (?, ?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, talk.getTitle());
                statement.setString(2, talk.getDescription());
                statement.setString(3, talk.getTopic());
                statement.executeUpdate();
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        talk.setId(generatedKeys.getInt(1));
                        return talk.getTitle() + "inserted with id = "+ talk.getId();
                    }
                    return "No id";
                }
            }
        }
    }

    private Talk mapToTalk(ResultSet result) throws SQLException{
        Talk talk = new Talk();
        talk.setId(result.getInt(1));
        talk.setTitle(result.getString("title"));
        talk.setDescription(result.getString("description"));
        talk.setTopic(result.getString("topic"));
        return talk;
    }

        public List<Talk> listAll() throws SQLException {
            return list("SELECT * FROM conference_talk", result ->mapToTalk(result));
        }

    public Talk getTalk(int id) throws SQLException {
        return getSingleObject("select * FROM conference_talk where id = " + id, result -> mapToTalk(result));
    }


    public DataSource createDataSource() {
        readPropertiesFile();
        SQLServerDataSource dataSource = new SQLServerDataSource();
        dataSource.setUser(dbUserName);
        dataSource.setPassword(dbPassword);
        dataSource.setServerName(dbUrl);
        dataSource.setDatabaseName(dbDatabase);
        dataSource.setEncrypt(dbEncrypt);
        dataSource.setTrustServerCertificate(dbTrustServerCertificate);
        dataSource.setHostNameInCertificate(dbHostNameInCertificate);
        dataSource.setLoginTimeout(dbLoginTimeout);

        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    private void readPropertiesFile() {
        Properties props = new Properties();
        String dbSettingsPropertyFile = "JDBCSettings.properties";
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(dbSettingsPropertyFile));
        } catch (IOException e) {
            System.out.println("File not found");
        }

        // Get each property value
        dbUrl = props.getProperty("dbUrl");
        dbDatabase = props.getProperty("dbDatabase");
        dbUserName = props.getProperty("dbUser");
        dbPassword = props.getProperty("dbPass");
        dbEncrypt = Boolean.parseBoolean(props.getProperty("dbEncrypt"));
        dbTrustServerCertificate = Boolean.parseBoolean(props.getProperty("dbTrustServerCertificate"));
        dbHostNameInCertificate = props.getProperty("dbHostNameInCertificate");
        dbLoginTimeout = Integer.parseInt(props.getProperty("dbLoginTimeout"));


    }
}
