package no.kristiania.pgr200.database.main;


import org.junit.Test;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

public class ClientTest {

    @Test
    public void shouldCorrectlyParseArguments() {
        // Add (POST)
        Client.requestStringBuilder(new String[]{"add", "-title", "1", "-description", "2", "-top", "3"});
        assertEquals(Client.getArgumentsMap().get("Title"), "1");
        assertEquals(Client.getArgumentsMap().get("Description"), "2");
        assertEquals(Client.getArgumentsMap().get("Topic"), "3");

        // Update (POST)
        Client.requestStringBuilder(new String[]{"update", "1", "-title", "new title", "-description", "new description", "-topic", "new topic"});
        assertThat(Client.getArgumentsMap().get("id")).isEqualToIgnoringCase("1");
        assertThat(Client.getArgumentsMap().get("Title")).isEqualToIgnoringCase("new title");
        assertThat(Client.getArgumentsMap().get("Description")).isEqualToIgnoringCase("new description");
        assertThat(Client.getArgumentsMap().get("Topic")).isEqualToIgnoringCase("new topic");

        //Show (GET)
        Client.requestStringBuilder(new String[]{"show", "1"});
        assertThat(Client.getArgumentsMap().get("id")).isEqualToIgnoringCase("1");

    }

    @Test
    public void shouldBuildRequestString() {

        // ADD
        Client.requestStringBuilder(new String[]{"add", "-title", "1 2", "-description", "2", "-top", "3"});
        assertEquals(Client.getRequestString(), "Title=1+2&Description=2&Topic=3");

        //UPDATE
        Client.requestStringBuilder(new String[]{"update", "1", "-title", "new title", "-description", "new description", "-topic", "new topic"});
        assertThat(Client.getRequestString()).isEqualToIgnoringCase("id=1&Title=new+title&description=new+description&topic=new+topic");

        //SHOW
        Client.requestStringBuilder(new String[]{"show", "1"});
        assertEquals(Client.getRequestString(), "id=1");
    }
}
