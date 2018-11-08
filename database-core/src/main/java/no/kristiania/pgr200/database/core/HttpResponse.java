package no.kristiania.pgr200.database.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class HttpResponse {
    String responseBody;
    private InputStream input;

    public HttpResponse(Socket socket) {
        try {
            input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            extractData(reader);

        } catch (IOException e) {
            System.out.println("Something went wrong when handling the http response");
            ;
        }

    }

    private void extractData(BufferedReader reader) throws IOException {
        String line;
        StringBuilder body = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            body.append(line).append("\r\n");
            if(line.isEmpty()){
                break;
            }
        }
        responseBody = body.toString();
    }

    public void printResponse(){
        System.out.println(responseBody);
    }

}

