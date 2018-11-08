package no.kristiania.pgr200.database.core;

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
    private String dbConnUrl, dbPassword, dbUserName;
    private Database db;


    public Server(){

        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            System.out.println("io exception thrown in server");
        }
        assert serverSocket != null;
        this.port = serverSocket.getLocalPort();
        System.out.println("Server online on port: " + port);
        db = new Database(createDataSource());
        new Thread(this::startServer).start();

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
                String[] requestLine = readNextLine(input).split("\\+");


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
                    db.insertTalk(newTalk);
                    output.write(("Inserted with id" + newTalk.getId()).getBytes());
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

                if (requestLine[0].startsWith("show")) {
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

    public int getPort() {
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
        System.out.println(currentLine);
        return currentLine.toString();
    }

    public DataSource createDataSource() {
        PGPoolingDataSource dataSource = new PGPoolingDataSource();
        readPropertiesFile();
        dataSource.setUrl(dbConnUrl);
        dataSource.setUser(dbUserName);
        dataSource.setPassword(dbPassword);
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    private void readPropertiesFile() {
        Properties props = new Properties();
        String dbSettingsPropertyFile = "eksamen.properties";
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(dbSettingsPropertyFile));
        } catch (IOException e) {
            System.out.println("File not found");
        }

        // Get each property value
        dbConnUrl = props.getProperty("db.conn.url");
        dbUserName = props.getProperty("db.username");
        dbPassword = props.getProperty("db.password");

    }

}
