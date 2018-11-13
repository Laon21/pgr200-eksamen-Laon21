package no.kristiania.pgr200.database.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        server.stopServer();
    }

    @Test
    public void shouldInsertListUpdateAndShowTalk() {
        Server server = new Server(0);
        Database tempDb = new Database(createTempDataSource());
        Server.setDb(tempDb);
        HttpRequest addRequest = new HttpRequest("localhost", server.getPort(), "add", "Title=1&Description=2&Topic=3");
        HttpResponse addResponse = addRequest.execute();
        addResponse.printResponse();
        assertThat(tempDb.getTalk(1).getTitle()).isEqualToIgnoringCase("1");

        HttpRequest listRequest = new HttpRequest("localhost", server.getPort(), "list", "STRING_NOT_USED");
        HttpResponse listResponse = listRequest.execute();
        assertThat(listResponse.responseBody).isNotEmpty();
        listResponse.printResponse();

        HttpRequest updateRequest = new HttpRequest("localhost", server.getPort(), "update", "id=1&Title=new+talk");
        HttpResponse updateResponse = updateRequest.execute();
        updateResponse.printResponse();
        assertThat(tempDb.getTalk(1).getTitle()).isEqualToIgnoringCase("new talk");

        HttpRequest showRequest = new HttpRequest("localhost", server.getPort(), "show", "id=1");
        HttpResponse showResponse = showRequest.execute();
        showResponse.printResponse();
        assertThat(tempDb.getTalk(1).getTitle()).isEqualToIgnoringCase("new talk");

    }

    @Test
    public void serverShouldStartAndStop() {
        Server server = new Server(0);
        Database tempDb = new Database(createTempDataSource());
        Server.setDb(tempDb);
        HttpRequest addRequest = new HttpRequest("localhost", server.getPort(), "stopserver", "STRING_NOT_USED");
        HttpResponse addResponse = addRequest.execute();
        addResponse.printResponse();
        assertTrue(server.doStop);
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