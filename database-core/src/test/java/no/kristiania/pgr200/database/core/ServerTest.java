package no.kristiania.pgr200.database.core;

import org.junit.Test;

public class ServerTest {

@Test
    private void shoulGetRequestAndRespond(){
    Server server = new Server(0);

    HttpRequest request = new HttpRequest("localhost", server.getPort(), "add", "Titel=hei&Description=Test&Topic=12");

    }

}
