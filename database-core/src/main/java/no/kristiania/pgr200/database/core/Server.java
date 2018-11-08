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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.microsoft.sqlserver.jdbc.StringUtils.isInteger;

public class Server {

    private ServerSocket serverSocket;
    private int port;
    private String dbConnUrl, dbPassword, dbUserName;
    private Database db;


    public Server(int port) {

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("io exception thrown in server");
        }
        assert serverSocket != null;
        this.port = serverSocket.getLocalPort();
        System.out.println("Server online on port: " + port);
        db = new Database(createDataSource());
        new Thread(this::startServer).start();

    }

    public static void main(String[] args) {
        Server localServer = new Server(10080);

    }

    private void startServer() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established");
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();

                String[] requestLine = readNextLine(input).split(" ");


                if (requestLine[0].equalsIgnoreCase("POST")) {
                    Map<String, String> parameters = new HashMap<>();
                    for (String param : requestLine[2].split("&")) {
                        parameters.put(param.split("=")[0].toLowerCase(), param.split("=")[1].toLowerCase());
                    }
                    if (requestLine[1].toLowerCase().contains("update")) {
                        try {
                            db.updateTalk(parameters);
                            output.write(("Updated element with id" + parameters.get("id") + "\r\n").getBytes());
                        } catch (SQLException e) {
                            System.out.println("failed to update object");
                        }
                    } else {
                        Talk newTalk = new Talk();
                        newTalk.setTitle(parameters.get("title"));
                        newTalk.setDescription(parameters.get("description"));
                        newTalk.setTopic(parameters.get("topic"));

                        db.insertTalk(newTalk);
                        output.write(("Inserted with id" + newTalk.getId() + "\r\n").getBytes());
                    }
                } else if ((requestLine[1].split("/")[3]).equalsIgnoreCase("list")) {
                    try {
                        for (Talk talk : db.listAll()) {
                            output.write(((talk).toString() + "\r\n").getBytes());
                        }
                        if (db.listAll().isEmpty()) {
                            output.write(("There was nothing to print!?").getBytes());
                        }
                    } catch (SQLException e) {
                        System.out.println("Failed to list talks");
                        break;
                    }

                } else if (isInteger(requestLine[1].split("/")[3])) {
                    try {
                        output.write(db.getTalk(Integer.parseInt(requestLine[1].split("/")[3])).toString().getBytes());
                        output.write(("\r\n").getBytes());
                    } catch (SQLException e) {
                        System.out.println("Something went wrong");
                        break;
                    } catch (NullPointerException e) {
                        System.out.println("No element with that id found");
                        break;
                    }
                }
                output.write(("Connection: closed \r\n").getBytes());
                output.flush();
                clientSocket.close();
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
