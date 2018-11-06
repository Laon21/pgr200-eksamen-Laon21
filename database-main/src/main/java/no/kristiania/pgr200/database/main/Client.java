package no.kristiania.pgr200.database.main;

import no.kristiania.pgr200.database.core.Server;

import java.io.IOException;
import java.net.Socket;


public class Client {

    static Server server;

    public static void main(String[] args) throws IOException {
            server = new Server();

    }
    //requestLine should look like this
    // "add" param "-ti% $title -de% $description -to% $topic"
    // "list all"
    // "show" Param "id"
    // "update" NYI
    public void sendRequest(String method,String target, String request){
        if(method.equalsIgnoreCase("list all"))
        try {
            Socket socket = new Socket("localhost",server.getPort());
            socket.getOutputStream().write(("GET " + target +" "+ request + "\r\n").getBytes());
            socket.getOutputStream().write(("\r\n").getBytes());
        } catch (IOException e) {
            System.out.println("failed to send GET request");
        }

        if(method.equalsIgnoreCase("post")){
            try {
            Socket socket = new Socket("localhost",server.getPort());
            socket.getOutputStream().write(("POST http://localhost/ap HTTP/1.1\r\n").getBytes());
            socket.getOutputStream().write((request).getBytes());
            socket.getOutputStream().write(("\r\n").getBytes());
        } catch (IOException e) {
            System.out.println("failed to send POST request");
        }
        }

    }

}
