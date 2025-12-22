//package SingleThreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Simple single-threaded TCP server example.
 *
 * <p>
 * This Server listens on a fixed port and accepts incoming connections in a
 * simple loop. For each accepted connection it creates a PrintWriter for
 * sending data to the client and a BufferedReader for reading data from the
 * client. In this minimal example it reads a single line from the client and
 * responds with a greeting.
 * </p>
 *
 * <p>
 * Notes / caveats:
 * - This implementation is intentionally minimal and single-threaded: while
 *   handling a connected client the server will not accept other connections.
 * - Streams and sockets opened for each accepted connection are closed here,
 *   but in a production system you should use try-with-resources and add more
 *   robust request handling, timeouts and error handling.
 * - The ServerSocket has a 20 second accept timeout configured (setSoTimeout).
 *   If no connection arrives within that time, accept() will throw a
 *   java.net.SocketTimeoutException. This example logs exceptions and continues
 *   running inside the loop; adapt this behavior to your application's needs.
 * </p>
 */
public class Server {

    /**
     * Run the server: bind to a port, accept connections, read one line from
     * the client, and send a simple response.
     *
     * @throws IOException when there is an I/O error creating the ServerSocket
     * @throws UnknownHostException not used here but declared for compatibility
     */
    public void run() throws IOException, UnknownHostException{
        // The port this server will listen on.
        int port = 8010;
        // Create a ServerSocket bound to the specified port. This will listen
        // for incoming TCP connection requests.
        ServerSocket socket = new ServerSocket(port);
        // Configure a timeout (in milliseconds) for accept(). If no connection
        // is received within this timeout, accept() will throw a
        // SocketTimeoutException. Here it's set to 20 seconds (20000 ms).
        socket.setSoTimeout(20000);

        // Main server loop: continually accept and handle connections.
        // NOTE: This implementation is single-threaded and only demonstrates the
        // basics of accepting a connection and sending a response. For a
        // production server you would typically hand each connection off to a
        // thread pool and perform structured request parsing/response.
        while (true) {
            try{
                System.out.println("Server is listing on port:"+port);
                // Wait for a client to connect. This blocks until a connection is
                // made or the socket times out (see setSoTimeout above).
                Socket acceptedConnection = socket.accept();
                // Log the remote client's address for debugging.
                System.out.println("Connected to:" +acceptedConnection.getRemoteSocketAddress());

                // Create a PrintWriter for sending text data to the client.
                // The second argument 'true' enables auto-flush on newline which
                // is convenient for simple line-oriented protocols.
                PrintWriter toClient = new PrintWriter(acceptedConnection.getOutputStream(), true);
                // Create a BufferedReader to read text data from the client.
                // In this minimal example we read a single line and then close
                // the connection. For real protocols you would loop and parse
                // messages according to the protocol specification.
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(acceptedConnection.getInputStream()));
                String clientMessage = fromClient.readLine();
                System.out.println("Client says: "+clientMessage);
                // Send a simple greeting to the connected client.
                toClient.println("Hello world from server!");
                // Close per-connection resources. In production prefer
                // try-with-resources to ensure these are closed even on errors.
                fromClient.close();
                toClient.close();
                acceptedConnection.close();
            }catch(Exception e){
                // Print stack trace for debugging. In a real service prefer
                // structured logging and consider whether the server should
                // continue running or terminate depending on the exception.
                e.printStackTrace();
            }
        }
    }

    /**
     * Entry point for the Server application.
     *
     * It creates a Server instance and calls run(). Any thrown exception is
     * printed to stderr; in production prefer structured logging and
     * appropriate exit codes.
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
