package no.kristiania.pgr200.database.main;

import no.kristiania.pgr200.database.core.Server;

import java.io.IOException;
import java.net.Socket;


public class Client {

    static Server server;
    final String target = "localhost/api";

    public static void main(String[] args) throws IOException {
        server = new Server();

        if(args.length == 0){
            System.out.println("Try with an argument");
            System.exit(1);
        }
    }

    //requestLine should look like this
    // "add" param "-ti% $title -de% $description -to% $topic"
    // "list"
    // "show" Param "id"
    // "update" NYI
    public void sendRequest(String method, String request) {
        if (method.equalsIgnoreCase("list"))
            try {
                Socket socket = new Socket("localhost", server.getPort());
                socket.getOutputStream().write(("GET " + target + "/" + request + "\r\n").getBytes());
                socket.getOutputStream().write(("\r\n").getBytes());
            } catch (IOException e) {
                System.out.println("failed to send list request");
            }

        if (method.equalsIgnoreCase("add")) {
            try {
                Socket socket = new Socket("localhost", server.getPort());
                socket.getOutputStream().write(("POST " + target + "/" + request + "HTTP/1.1\r\n").getBytes());
                socket.getOutputStream().write((request).getBytes());
                socket.getOutputStream().write(("\r\n").getBytes());
            } catch (IOException e) {
                System.out.println("failed to send POST request");
            }
        }
        if (method.equalsIgnoreCase("show")) {
            try {
                Socket socket = new Socket("localhost", server.getPort());
                socket.getOutputStream().write(("GET " + target + "/" + request + "HTTP/1.1\r\n").getBytes());
                socket.getOutputStream().write((request).getBytes());
                socket.getOutputStream().write(("\r\n").getBytes());
            } catch (IOException e) {
                System.out.println("failed to send show request");
            }
        }


    }

}
