package no.kristiania.pgr200.database.main;

import no.kristiania.pgr200.database.core.HttpRequest;
import no.kristiania.pgr200.database.core.HttpResponse;

import java.util.LinkedHashMap;


public class Client {

    private static LinkedHashMap<String, String> arguments;
    static StringBuilder httpString;
    private static String target = "localhost";
    static String method;
    static String requestString;


    /**
     * Runs when program is started, takes args from the user and executes a request to the server.
     * Server then sends back the result of the operation and the program is finished.
     * @param args Arguments from the user
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                noArguments();
                System.exit(1);
            }
            requestStringBuilder(args);
            sendRequest(target, 10080, method, requestString).printResponse();
        } catch (NullPointerException e) {
            System.out.println("Check if server is online before trying again");
        }
    }

    /**
     *Runs if no arguments is provided
     */
    private static void noArguments() {
        System.out.println("Try with an argument");
        System.out.println("add -title {title} -description {description} -topic {topic}");
        System.out.println("list");
        System.out.println("show {id}");
        System.out.println("update {id} -{column} {value}");
        System.out.println("resetDb");

    }

    /**
     * Creates a HTTP request from arguments and sends it to the target returning a HTTP response.
     * @param target Server location
     * @param port Server port number
     * @param method What you want to happen
     * @param requestString Payload
     * @return Response from the server
     */
    private static HttpResponse sendRequest(String target, int port, String method, String requestString) {
        HttpRequest request = new HttpRequest(target, port, method, requestString);
        return request.execute();
    }

    /**
     * Takes the provided arguments and parses them to build it into a request string to send to the server
     * parseArgs() Takes the string array and puts it into a LinkedHashMap
     * Key and value are then  combined with a "=" sign and ends with a "&"
     * Last & is deleted after the string is build
     * Title "1" topic "2 1" -> Title=1&Topic=2+1
     * @param args arguments provided by the user
     */
    private static void requestStringBuilder(String[] args) {
        httpString = new StringBuilder();
        parseArgs(args);
        arguments.keySet()
                .forEach(key ->
                        httpString
                                .append(key)
                                .append("=")
                                .append(arguments.get(key))
                                .append("&")
                );
        httpString.deleteCharAt(httpString.lastIndexOf("&"));
        method = httpString.toString().split(" ")[0];
        requestString = httpString.toString().replace(" ", "+");
        httpString = new StringBuilder(requestString);
        httpString.delete(0, httpString.indexOf("+") + 1);
        requestString = httpString.toString();

    }

    /**
     * Used in RequestStringBuilder
     * Parses the arguments from the user and adds it to a LinkedHashMap that is used to  build the request string
      * @param args provided by the user
     * @return LinkedHashMap with the argument values
     */
    public static LinkedHashMap<String, String> parseArgs(String[] args) {
        arguments = new LinkedHashMap<>();
        httpString.append(args[0]).append(" ");
        if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("update")) {
            if (args[0].equalsIgnoreCase("update")) {
                arguments.put("id", args[1]);
            }
            for (int i = 0; i < args.length - 1; i++) {
                if (args[i].toLowerCase().startsWith("-ti")) {
                    arguments.put("Title", args[i + 1]);
                } else if (args[i].toLowerCase().startsWith("-de")) {
                    arguments.put("Description", args[i + 1]);
                } else if (args[i].toLowerCase().startsWith("-to")) {
                    arguments.put("Topic", args[i + 1]);
                }
            }
        } else if (args[0].equalsIgnoreCase("show")) {
            arguments.put("id", args[1]);
        } else if (args[0].equalsIgnoreCase("list")) {
            arguments.put("id", "all");
        } else if (args[0].equalsIgnoreCase("resetDb")) {
            arguments.put("ground", "zero");
        }
        return arguments;
    }

    public static LinkedHashMap<String, String> getArgumentsMap() {
        return arguments;
    }

    public static String getRequestString() {
        return requestString;
    }
}