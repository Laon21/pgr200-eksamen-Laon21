package no.kristiania.pgr200.database.core;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("deprecation")
public class Server {

    private ServerSocket serverSocket;
    private int port;
    private String dataSourceUrl, dataSourcePassword, dataSourceUsername;
    private static Database db;
    protected Boolean doStop = false;
    protected OutputStream output;
    protected InputStream input;


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

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Server localServer = new Server(10080);
    }

    /**
     * Waits for a connection then executes the request from the client
     */
    private void startServer() {
        while (!doStop) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established");
                input = clientSocket.getInputStream();
                output = clientSocket.getOutputStream();
                String[] requestLine = readNextLine(input).split(" ");

                if (requestLine[0].equalsIgnoreCase("POST")) {
                    postRequest(requestLine);
                } else if ((requestLine[1].split("/")[3]).equalsIgnoreCase("list")) {
                    listAllTalks();
                } else if ((requestLine[1].split("/")[3]).equalsIgnoreCase("resetDb")) {
                    resetDb();
                } else if (isInteger(requestLine[1].split("/")[3])) {
                    showElementWithId(requestLine[1]);
                } else if ((requestLine[1].split("/")[3]).equalsIgnoreCase("stopserver")) {
                    output.write(("Shutting down server... \r\n").getBytes());
                    stopServer();
                }
                output.write(("Connection: closed \r\n").getBytes());
                output.flush();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Something went wrong when handling input or output");
            }
        }
    }

    protected synchronized void stopServer() {
        System.out.println("Closing connection...");
        outputWriter("Server shutting down");
        this.doStop = true;
    }

    /**
     * If its a post request this is run
     *
     * @param requestLine HTTP requestLine from the client
     */
    private void postRequest(String[] requestLine) {
        Map<String, String> parameters = new HashMap<>();
        for (String param : requestLine[2].split("&")) {
            parameters.put(param.split("=")[0].toLowerCase(), param.split("=")[1].replaceAll("\\+", " ").toLowerCase());
        }
        if (requestLine[1].toLowerCase().contains("update")) {
            updateTalkWithId(parameters);
        } else {
            insertNewTalk(parameters);
        }
    }

    /**
     * Creates a new Talk object and inserts it into the database
     *
     * @param parameters Map containing keys (columns) and value for the new object
     */
    private void insertNewTalk(Map<String, String> parameters) {
        Talk newTalk = new Talk();
        newTalk.setTitle(parameters.get("title"));
        newTalk.setDescription(parameters.get("description"));
        newTalk.setTopic(parameters.get("topic"));
        db.insertTalk(newTalk);
        outputWriter("Inserted " + newTalk.getTitle() + " with id" + newTalk.getId() + "\r\n");
    }

    /**
     * Updates a element in the database with the provided ID
     *
     * @param parameters Map containing the new values for columns in the database. Key is column name
     */
    public void updateTalkWithId(Map<String, String> parameters) {
        db.updateTalk(parameters);
        outputWriter("Updated id " + parameters.get("id") + "\r\n");

    }

    /**
     * Gets all elements in the database and returns them to the client
     */
    private void listAllTalks() {
        for (Talk talk : db.listAll()) {
            outputWriter((talk).toString() + "\r\n");
        }
        if (db.listAll().isEmpty()) {
            outputWriter("There was nothing to print.");
        }
    }

    /**
     * Tries to find the element with provided ID in the database
     *
     * @param request HTTP request
     */
    private void showElementWithId(String request) {
        try {
            outputWriter(db.getTalk(Integer.parseInt(request.split("/")[3])).toString());
            outputWriter("\r\n");
        } catch (NullPointerException e) {
            System.out.println("No element with that id found.");
        }
    }

    /**
     * This makes the database drop all information and start again
     */
    private void resetDb() {
        db.resetDb();
        outputWriter("All eleMENts, eleWOMENts and eleCHILDRENts was deleted. I hope you're happy. \r\n");
        db = new Database(createDataSource());
    }

    /**
     * Checks if the string is an Integer
     *
     * @param input HTTP requestString
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

    public void outputWriter(String outputString) {
        try {
            output.write((outputString).getBytes());
        } catch (IOException e) {
            System.out.println("failed to write output");
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
        dataSource.setUrl(dataSourceUrl);
        dataSource.setUser(dataSourceUsername);
        dataSource.setPassword(dataSourcePassword);
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
            String dbSettingsPropertyFile = "innlevering.properties";
            props.load(getClass().getClassLoader().getResourceAsStream(dbSettingsPropertyFile));
        } catch (IOException e) {
            System.out.println("file not found");
        }
        dataSourceUrl = props.getProperty("db.conn.url");
        dataSourceUsername = props.getProperty("db.username");
        dataSourcePassword = props.getProperty("db.password");

    }

    public static void setDb(Database tempDb) {
        Server.db = tempDb;
    }

}
