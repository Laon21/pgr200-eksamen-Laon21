package no.kristiania.pgr200.database.core;

import java.util.HashMap;

public class ArgumentsParser {
	HashMap<String, String> arguments = new HashMap<String, String>();
	
	public ArgumentsParser() {
		
	}
	
	public HashMap<String, String> parseArgs(String[] args) {
		for(int i = 0; i < args.length; i++) {
    		if(args[i].toLowerCase().startsWith("-ti")) {
    			arguments.put("Title", args[i + 1]);
    		} else if(args[i].toLowerCase().startsWith("-de")) {
    			arguments.put("Description", args[i + 1]);
    		} else if(args[i].toLowerCase().startsWith("-to")) {
    			arguments.put("Topic", args[i + 1]);
    		}
    		
    		}
		return arguments;
        }
		
}