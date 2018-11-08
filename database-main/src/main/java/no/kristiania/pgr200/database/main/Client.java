package no.kristiania.pgr200.database.main;
import no.kristiania.pgr200.database.core.HttpRequest;
import no.kristiania.pgr200.database.core.HttpResponse;
import no.kristiania.pgr200.database.core.Server;
import java.util.LinkedHashMap;


public class Client {

    private static LinkedHashMap<String, String> arguments;
    static StringBuilder httpString;

    private static String target = "localhost";
    static String method;
    static String requestString;


    public static void main(String[] args){
        if (args.length == 0) {
            noArguments();
        }
        requestStringBuilder(args);
        String method = httpString.toString().split(" ")[0];
        String requestString = httpString.toString().split(" ")[1].replace(" ", "+");
        sendRequest(target,10080,method, requestString).printResponse();
    }

    private static void noArguments() {
        System.out.println("Try with an argument");
        System.out.print("add -title {title} -description {description} -topic {topic}");
        System.out.print("list");
        System.out.print("show {id}");
        System.out.print("update {id} -{column} {value}");
        System.out.print("resetdb");

    }

    private static HttpResponse sendRequest(String target, int port, String method, String requestString) {
        HttpRequest request = new HttpRequest(target,port ,method, requestString);
        return request.execute();
    }

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
        requestString = httpString.toString().split(" ")[1].replace(" ", "+");

    }

    public static LinkedHashMap<String, String> parseArgs(String[] args) {
        arguments = new LinkedHashMap<>();
        httpString.append(args[0]).append(" ");
        if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("update")) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].toLowerCase().startsWith("-ti")) {
                    arguments.put("Title", args[i + 1]);
                } else if (args[i].toLowerCase().startsWith("-de")) {
                    arguments.put("Description", args[i + 1]);
                } else if (args[i].toLowerCase().startsWith("-to")) {
                    arguments.put("Topic", args[i + 1]);
                }
            }
        }
        if (args[0].equalsIgnoreCase("show")) {
            arguments.put("id", args[1]);
        }

        if (args[0].equalsIgnoreCase("list")) {
            arguments.put("id", "all");
        }

        return arguments;
    }


}