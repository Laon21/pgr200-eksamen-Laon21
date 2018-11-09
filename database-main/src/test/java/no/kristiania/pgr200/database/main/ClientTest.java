package no.kristiania.pgr200.database.main;


import no.kristiania.pgr200.database.core.Talk;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;

public class ClientTest {

    /*@Test
    public void parseArguments() throws IOException {
        Talk talk = new Talk();
        talk.setTitle("1");
        talk.setDescription("2");
        talk.setTopic("3");

        Client client = new Client();
        client.main(new String[]{"add", "-title", "1", "-description", "2", "-top", "3"});
    }*/
    
    @Test
    public void shouldCorrectlyParseArguments() {
    	Client.main(new String[]{"add", "-title", "1", "-description", "2", "-top", "3"});
    	assertEquals(Client.getArgumentsMap().get("Title"), "1");
    	assertEquals(Client.getArgumentsMap().get("Description"), "2");
    	assertEquals(Client.getArgumentsMap().get("Topic"), "3");
    }


}
