package no.kristiania.pgr200.database.main;


import no.kristiania.pgr200.database.core.Talk;
import org.junit.Test;

import java.io.IOException;

public class ClientTest {

    @Test
    public void parseArguemnts() throws IOException {
        Talk talk = new Talk();
        talk.setTitle("1");
        talk.setDescription("2");
        talk.setTopic("3");

        Client client = new Client();
        client.main(new String[]{"add", "-title", "1", "-description", "2", "-top", "3"});


    }


}
