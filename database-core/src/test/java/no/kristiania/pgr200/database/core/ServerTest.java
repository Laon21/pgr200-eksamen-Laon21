package no.kristiania.pgr200.database.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;

public class ServerTest {

    /*@Test
    public void shouldUpdateTalk() {
    	Server server = new Server(0);
    	HashMap<String,String> params = new HashMap<String,String>();
    	params.put("id", "101");
    	
    	Talk talk = new Talk();
    	talk.setTitle("test title");
    	talk.setDescription("test description");
    	talk.setTopic("test topic");
    	talk.setId(101);
    	
    	try {
			server.updateTalkWithId(server.output, params);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }*/
    
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

	private DataSource createTempDataSource() {
		JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
	}
}