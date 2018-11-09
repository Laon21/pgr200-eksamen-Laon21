package no.kristiania.pgr200.database.core;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class HttpRequest {

    private String command;
    private String hostname, method, request;
    private int port;
    private final String path = "localhost:/api/talks";


    public HttpRequest(String hostname, int port, String method, String request) {
        this.hostname = hostname;
        this.port = port;
        this.command = method;
        if (method.equalsIgnoreCase("add") || method.equalsIgnoreCase("update")) {
            this.method = "POST";
        } else {
            this.method = "GET";
        }
        this.request = request;
    }

    public HttpResponse execute() {

        try (Socket socket = new Socket(hostname, port)) {

            writeRequest(socket.getOutputStream());
            return new HttpResponse(socket);

        } catch (UnknownHostException e) {
            System.out.println("unknown host exception when executing request");
        } catch (IOException e) {
            System.out.println("IO exception when trying to execute request");
        }

        return null;

    }

    public String getHostname() {
        return hostname;
    }


    public void writeRequest(OutputStream output) throws IOException {
        if (command.equalsIgnoreCase("add")) {
            output.write((method + " " + path + " " + request + " HTTP/1.1\r\n").getBytes());
            output.write("\r\n\r\n".getBytes());
        } else if (command.equalsIgnoreCase("show")) {
            output.write((method + " " + path + "/" + request.split("=")[1] + " HTTP/1.1\r\n").getBytes());
            output.write("\r\n\r\n".getBytes());
        } else if (command.equalsIgnoreCase("list")) {
            output.write((method + " " + path + "/" + command  + " HTTP/1.1\r\n").getBytes());
            output.write("\r\n\r\n".getBytes());
        } else if (command.equalsIgnoreCase("update")) {
            output.write((method + " " + path + "/" + command +" "+ request  + " HTTP/1.1\r\n").getBytes());
            output.write("\r\n\r\n".getBytes());
        } else if (command.equalsIgnoreCase("resetdb")) {
            output.write((method + " " + path + "/" + command  + " HTTP/1.1\r\n").getBytes());
            output.write("\r\n\r\n".getBytes());
        }
        

    }


}
