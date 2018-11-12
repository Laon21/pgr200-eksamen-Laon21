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
    private Database db;
    private Boolean doStop = false;


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

    private synchronized void stopServer() {
        System.out.println("Closing connection...");
        this.doStop = true;
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
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();
                String[] requestLine = readNextLine(input).split(" ");

                if (requestLine[0].equalsIgnoreCase("POST")) {
                    postRequest(output, requestLine);
                } else if ((requestLine[1].split("/")[3]).equalsIgnoreCase("list")) {
                    listAllTalks(output);
                } else if ((requestLine[1].split("/")[3]).equalsIgnoreCase("resetDb")) {
                    resetDb(output);
                } else if (isInteger(requestLine[1].split("/")[3])) {
                    showElementWithId(output, requestLine[1]);
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

    /**
     * If its a post request this is run
     *
     * @param output      OutputStream for the http response
     * @param requestLine HTTP requestLine from the client
     * @throws IOException OutputStream
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

    /**
     * Creates a new Talk object and inserts it into the database
     *
     * @param output     OutputStream for the http response
     * @param parameters Map containing keys (columns) and value for the new object
     * @throws IOException OutputStream
     */
    private void insertNewTalk(OutputStream output, Map<String, String> parameters) throws IOException {
        Talk newTalk = new Talk();
        newTalk.setTitle(parameters.get("title"));
        newTalk.setDescription(parameters.get("description"));
        newTalk.setTopic(parameters.get("topic"));
        db.insertTalk(newTalk);
        output.write(("Inserted " + newTalk.getTitle() + " with id" + newTalk.getId() + "\r\n").getBytes());
    }

    /**
     * Updates a element in the database with the provided ID
     *
     * @param output     OutputStream for the http response
     * @param parameters Map containing the new values for columns in the database. Key is column name
     * @throws IOException OutputStream
     */
    private void updateTalkWithId(OutputStream output, Map<String, String> parameters) throws IOException {
        db.updateTalk(parameters);
        output.write(("Updated element with id" + parameters.get("id") + "\r\n").getBytes());
    }

    /**
     * Gets all elements in the database and returns them to the client
     *
     * @param output OutputStream for the http response
     * @throws IOException OutputStream
     */
    private void listAllTalks(OutputStream output) throws IOException {
        for (Talk talk : db.listAll()) {
            output.write(((talk).toString() + "\r\n").getBytes());
        }
        if (db.listAll().isEmpty()) {
            output.write(("There was nothing to print!?").getBytes());
        }
    }

    /**
     * Tries to find the element with provided ID in the database
<<<<<<< HEAD
     *
     * @param output  OutputStream for the http response
=======
     * @param output  OutputStream for the HTTP response
>>>>>>> da409aa2563c07ea6f07330b687b10d172fd07f1
     * @param request HTTP request
     * @throws IOException OutputStream
     */
    private void showElementWithId(OutputStream output, String request) throws IOException {
        try {
            output.write(db.getTalk(Integer.parseInt(request.split("/")[3])).toString().getBytes());
            output.write(("\r\n").getBytes());
        } catch (NullPointerException e) {
            System.out.println("No element with that id found");
        }
    }

    /**
     * This makes the database drop all information and start anew
     * Server will shut down to apply updates. #TODO separate DB and server to avoid this in the future
<<<<<<< HEAD
     *
     * @param output OutputStream for the http response
=======
     * @param output OutputStream for the HTTP response
>>>>>>> da409aa2563c07ea6f07330b687b10d172fd07f1
     */
    private void resetDb(OutputStream output) throws IOException {
        db.resetDb();
        output.write(("All eleMENts, eleWOMENts and eleCHILDRENts was deleted. I hope you're happy. \r\n").getBytes());
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

}
