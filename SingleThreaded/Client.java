//package SingleThreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Simple client that connects to a server socket on localhost and a fixed port,
 * sends a single line message, reads a single line response, prints it and closes the connection.
 *
 * This class is intentionally minimal and demonstrates basic blocking socket I/O:
 *  - create a Socket
 *  - wrap its input/output streams for convenient text I/O
 *  - send a line, receive a line
 *  - close streams and socket
 *
 * Note: In production code you would likely want to handle timeouts, reconnects,
 * and ensure resources are closed in finally blocks or use try-with-resources.
 */
public class Client {

    /**
     * Connects to a server running on localhost:8010, sends a greeting message,
     * reads one line of reply, prints it to standard output, then closes the connection.
     *
     * @throws UnknownHostException if the localhost address can't be resolved (very unlikely)
     * @throws IOException if an I/O error occurs when creating the socket or reading/writing streams
     */
    public void run() throws UnknownHostException, IOException{
        // server port to connect to
        int port = 8010;

        // resolve the server address — here we use localhost
        InetAddress address = InetAddress.getByName("localhost");

        // create a TCP socket connected to the server address/port
        Socket socket = new Socket(address, port);

        // wrap the socket output stream in a PrintWriter for convenient text output
        // the second arg 'true' enables auto-flushing on println()
        PrintWriter toSocket = new PrintWriter(socket.getOutputStream(), true);

        // wrap the socket input stream in a BufferedReader for convenient text input (readLine)
        BufferedReader fromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // send a single-line message to the server
        toSocket.println("Hello world from socket: " + socket.getLocalSocketAddress());

        // read a single line response from the server (blocking until available or connection closed)
        String line = fromSocket.readLine();

        // print what the server sent back
        System.out.println("Server says: " + line);

        // Close writers/readers and the socket to free resources.
        // In more robust code prefer try-with-resources or ensure close is called in finally.
        toSocket.close();
        fromSocket.close();
        socket.close();
    }

    /**
     * Entry point for running the client from the command line.
     * Creates a Client instance and invokes run(). Any thrown exceptions are printed.
     */
    public static void main(String[] args) {
        Client singleThreadedWebServer_Client  = new Client();
        try {
            singleThreadedWebServer_Client.run();
        } catch (Exception e) {
            // Print stack trace to help debugging if the connection fails or I/O error occurs.
            e.printStackTrace();
        }
    }
}