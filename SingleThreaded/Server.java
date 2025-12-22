//package SingleThreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A very small single-threaded server example.
 *
 * <p>
 * This Server listens on a fixed port and accepts incoming connections in a
 * simple loop. For each accepted connection it creates a PrintWriter for
 * sending data to the client and a BufferedReader for reading data from the
 * client. Currently it only writes a greeting message to the client.
 * </p>
 *
 * <p>
 * Notes / caveats:
 * - This implementation is intentionally minimal and single-threaded: while
 *   handling a connected client the server will not accept other connections.
 * - Streams and sockets opened for each accepted connection are not closed in
 *   this example; in a production system you must close resources (preferably
 *   using try-with-resources) and add proper request handling and error
 *   handling.
 * - The ServerSocket has a 20 second accept timeout configured (setSoTimeout).
 *   That means accept() will throw a java.net.SocketTimeoutException if no
 *   connection arrives within 20 seconds; in this code it isn't specifically
 *   handled and so will propagate out of run() (causing the program to print a
 *   stack trace in main()).
 * </p>
 */
public class Server {

    public void run() throws IOException, UnknownHostException{
        // The port this server will listen on.
        int port = 8010;
        // Create a ServerSocket bound to the specified port.
        // This will listen for incoming TCP connection requests.
        ServerSocket socket = new ServerSocket(port);
        // Configure a timeout (in milliseconds) for accept().
        // If no connection is received within this timeout, accept() will throw
        // a SocketTimeoutException. Here it's set to 20 seconds (20000 ms).
        socket.setSoTimeout(20000);
        // Main server loop: continually accept and handle connections.
        // NOTE: This implementation is single-threaded and only demonstrates the
        // basics of accepting a connection and sending a response.
        while (true) {
            try{
            System.out.println("Server is listing on port:"+port);
            // Wait for a client to connect. This blocks until a connection is
            // made or the socket times out (see setSoTimeout above).
            Socket acceptedConnection = socket.accept();
            // Log the remote client's address for debugging.
            System.out.println("Connected to:" +acceptedConnection.getRemoteSocketAddress());

            // Create a PrintWriter for sending text data to the client.
            // The second argument 'true' enables auto-flush on newline which is
            // convenient for simple line-oriented protocols.
            PrintWriter toClient = new PrintWriter(acceptedConnection.getOutputStream(), true);
            // Create a BufferedReader to read text data from the client.
            // In this minimal example we do not actually read from the client,
            // but this is how you would wrap the input stream for line-based IO.
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(acceptedConnection.getInputStream()));
            String clientMessage = fromClient.readLine();
            System.out.println("Client says: "+clientMessage);
            // Send a simple greeting to the connected client.
            toClient.println("Hello world from server!");
            fromClient.close();
            toClient.close();
            acceptedConnection.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Entry point for the Server application.
     *
     * It creates a Server instance and calls run(). Any exception thrown is
     * printed to stderr; in a production service you would typically add more
     * robust logging and error recovery.
     */
    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.run();
        } catch (Exception e) {
            // Print stacktrace to aid debugging - in production prefer structured logging.
            e.printStackTrace();
        }    }
}