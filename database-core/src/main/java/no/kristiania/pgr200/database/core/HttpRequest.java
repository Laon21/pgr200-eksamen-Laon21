package no.kristiania.pgr200.database.core;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class HttpRequest {

    private String hostname, method, request;
    private int port;
    private final String path = "localhost:/api/talks ";


    public HttpRequest(String hostname, int port, String method, String request) {
        this.hostname = hostname;
        this.port = port;
        if(method.equalsIgnoreCase("add")){
            this.method = "POST";
        }
        else{ this.method = "GET";}
        this.request = request;
    }

    public HttpResponse execute() {

        try (Socket socket = new Socket(hostname, port)) {

            writeRequest(socket.getOutputStream());
            return new HttpResponse(socket);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String getHostname() {
        return hostname;
    }



    public void writeRequest(OutputStream output) throws IOException {
        output.write((method +  " " + path + request + " HTTP/1.1\r\n").getBytes());
        output.write("\r\n\r\n".getBytes());

    }


}
