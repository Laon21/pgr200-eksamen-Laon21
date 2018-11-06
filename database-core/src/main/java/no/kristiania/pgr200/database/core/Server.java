package no.kristiania.pgr200.database.core;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Properties;

public class Server {

    private ServerSocket serverSocket;
    private int port;
    private InputStream input;
    private OutputStream output;

    private Database db;


    public Server() throws IOException {
        serverSocket = new ServerSocket(0);
        this.port = serverSocket.getLocalPort();
        System.out.println("Server online on port: " + port);
        db = new Database(db.createDataSource());
        new Thread(() -> startServer()).start();

    }

    private void startServer() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established");
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();


                //requestLine should look like this
                // "add -ti% $title -de% $description -to% $topic
                // "list"
                // "show $id"
                // "update" #TODO find a way to solve this
                String[] requestLine = readNextLine(input).split(" ");


                if (requestLine[0].startsWith("Add")) {
                    Talk newTalk = new Talk();
                    for (int i = 0; i < requestLine.length; i++) {
                        if (requestLine[i].toLowerCase().startsWith("-ti")) {
                            newTalk.setTitle(requestLine[++i]);
                        }
                        if (requestLine[i].toLowerCase().startsWith("-de")) {
                            newTalk.setDescription(requestLine[++i]);
                        }
                        if (requestLine[i].toLowerCase().startsWith("-to")) {
                            newTalk.setTopic(requestLine[++i]);
                        }
                    }
                    try {
                        db.insertTalk(newTalk);
                        output.write(("Inserted with id" + newTalk.getId()).getBytes());
                    } catch (SQLException e) {
                        System.out.println("Failed to insert");
                    }
                }

                if (requestLine[0].startsWith("list")) {
                    try {
                        for (Talk talk : db.listAll()) {
                            output.write((talk).toString().getBytes());
                        }
                    } catch (SQLException e) {
                        System.out.println("Failed to list talks");
                    }

                }

                if(requestLine[0].startsWith("show")){
                    try {
                        output.write((db.getTalk(Integer.parseInt(requestLine[1])).toString()).getBytes());
                    } catch (SQLException e) {
                        System.out.println("Something went wrong");
                    }
                }


            } catch (IOException e) {
                System.out.println("Something went wrong when handling input");
            }


        }
    }

    public int getPort(){
        return this.port;
    }

    private String readNextLine(InputStream input) throws IOException {
        StringBuilder currentLine = new StringBuilder();
        int line;
        while ((line = input.read()) != -1) {
            if (line == '\r') {
                input.mark(1);
                line = input.read();

                if (line != '\n') {
                    input.reset();
                }

                break;
            }
            currentLine.append((char) line);
        }
        return currentLine.toString();
    }

}
