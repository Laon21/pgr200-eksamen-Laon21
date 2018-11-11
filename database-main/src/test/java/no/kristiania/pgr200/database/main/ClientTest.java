package no.kristiania.pgr200.database.main;


import org.junit.Test;
import static org.junit.Assert.*;


public class ClientTest {
    
    @Test
    public void shouldCorrectlyParseArguments() {
    	Client.main(new String[]{"add", "-title", "1", "-description", "2", "-top", "3"});
    	assertEquals(Client.getArgumentsMap().get("Title"), "1");
    	assertEquals(Client.getArgumentsMap().get("Description"), "2");
    	assertEquals(Client.getArgumentsMap().get("Topic"), "3");
    }

	@Test
	public void shouldBuildRequestString() {
		Client.main(new String[]{"add", "-title", "1 2", "-description", "2", "-top", "3"});
		assertEquals(Client.getRequestString(), "Title=1+2&Description=2&Topic=3");
	}
}
