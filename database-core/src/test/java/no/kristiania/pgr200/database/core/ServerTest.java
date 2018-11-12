package no.kristiania.pgr200.database.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;

public class ServerTest {

    @Test
    public void shouldGetRequestAndRespond() {
        Server server = new Server(0);
        Database tempDb = new Database(createTempDataSource());
        Server.setDb(tempDb);
        HttpRequest request = new HttpRequest("localhost", server.getPort(), "add", "Title=1&Description=2&Topic=3");
        HttpResponse response = request.execute();
        response.printResponse();
        assertThat((response.responseBody)).isNotEmpty();
    }

    @Test
    public void shouldUpdateTalk() {
        Server server = new Server(0);
        Database tempDb = new Database(createTempDataSource());
        Server.setDb(tempDb);
        HttpRequest request = new HttpRequest("localhost", server.getPort(), "add", "Title=1&Description=2&Topic=3");
        HttpResponse response = request.execute();
        HashMap<String, String> params = new HashMap<String,String>();
        params.put("id", "1");
        params.put("title", "newtitle");
        try {
            server.updateTalkWithId(server.output, params);
            assertThat(tempDb.getTalk(Integer.parseInt(params.get("id"))).getTitle()).isEqualToIgnoringCase(params.get("title"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DataSource createTempDataSource() {
        return getDataSource();
    }

    static DataSource getDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }
}