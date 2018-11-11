package no.kristiania.pgr200.database.core;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
                    postRequest(output, requestLine);
                } else if ((requestLine[1].split("/")[3]).equalsIgnoreCase("list")) {
                    listAllTalks(output);
                } else if ((requestLine[1].split("/")[3]).equalsIgnoreCase("resetdb")) {
                    resetDb(output);
                } else if (isInteger(requestLine[1].split("/")[3])) {
                    showElementWithId(output, requestLine[1]);
                }
                output.write(("Connection: closed \r\n").getBytes());
                output.flush();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Something went wrong when handling input or output");
            }
        }
    }

    /**
     * If its a post request this is run
     *
     * @param output      HTTP response
     * @param requestLine HTTP request
     * @throws IOException OutputStream for the HTTP response
     */
    private void postRequest(OutputStream output, String[] requestLine) throws IOException {
        Map<String, String> parameters = new HashMap<>();
        for (String param : requestLine[2].split("&")) {
            parameters.put(param.split("=")[0].toLowerCase(), param.split("=")[1].replaceAll("\\+", " ").toLowerCase());
        }
        if (requestLine[1].toLowerCase().contains("update")) {
            updateTalkWithId(output, parameters);
        } else {
            insertNewTalk(output, parameters);
        }
    }

    private void insertNewTalk(OutputStream output, Map<String, String> parameters) throws IOException {
        Talk newTalk = new Talk();
        newTalk.setTitle(parameters.get("title"));
        newTalk.setDescription(parameters.get("description"));
        newTalk.setTopic(parameters.get("topic"));

        db.insertTalk(newTalk);
        output.write(("Inserted with id" + newTalk.getId() + "\r\n").getBytes());
    }

    private void updateTalkWithId(OutputStream output, Map<String, String> parameters) throws IOException {
        try {
            db.updateTalk(parameters);
            output.write(("Updated element with id" + parameters.get("id") + "\r\n").getBytes());
        } catch (SQLException e) {
            System.out.println("failed to update object");
        }
    }

    private void listAllTalks(OutputStream output) throws IOException {
        try {
            for (Talk talk : db.listAll()) {
                output.write(((talk).toString() + "\r\n").getBytes());
            }
            if (db.listAll().isEmpty()) {
                output.write(("There was nothing to print!?").getBytes());
            }
        } catch (SQLException e) {
            System.out.println("Failed to list talks");
        }
    }

    /**
     * Tries to find the element with provided ID in the database
     *
     * @param output  Output stream for the HTTP response
     * @param request HTTP request
     * @throws IOException OutputStream
     */
    private void showElementWithId(OutputStream output, String request) throws IOException {
        try {
            output.write(db.getTalk(Integer.parseInt(request.split("/")[3])).toString().getBytes());
            output.write(("\r\n").getBytes());
        } catch (SQLException e) {
            System.out.println("Something went wrong with the database");
        } catch (NullPointerException e) {
            System.out.println("No element with that id found");
        }
    }

    /**
     * This makes the database drop all information and start anew
     * Server will shut down to apply updates. #TODO separate DB and server to avoid this in the future
     * @param output Output stream for the http response for the client
     */
    private void resetDb(OutputStream output) throws IOException {
        try {
            db.resetdb();
            output.write(("All eleMENts, eleWOMENts and eleCHILDRENts was deleted. I hope you're happy. \r\n").getBytes());
            output.write(("Server will shutdown to apply changes \r\n").getBytes());
        } catch (SQLException e) {
            System.out.println("Failed to drop tables");
        }
    }

    /**
     * Checks if the string is an Integer
     *
     * @param input Http request
     * @return true if Integer, else false
     */
    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
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

    /**
     * Reads the properties file for Database information
     * Remember to change this to your local values
     */
    private void readPropertiesFile() {
        Properties props = new Properties();
        try {
            String dbSettingsPropertyFile = "./eksamen.properties";
            props.load(getClass().getClassLoader().getResourceAsStream(dbSettingsPropertyFile));
        } catch (IOException e) {
            System.out.println("file not found");
        }


        // Get each property value
        dbConnUrl = props.getProperty("db.conn.url");
        dbUserName = props.getProperty("db.username");
        dbPassword = props.getProperty("db.password");

    }

}
