package Server;

import java.net.*;
import java.io.*;

/**
 * The Server that initialises a ServerReceiver and a ServerSender for each user that joins the server
 * Opens a socket on the given port
 * Adds them to the GameTable and the ClientTable
 * @author Jack Morrison
 */

public class Server {

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.err.println("Usage: java Server port");
			System.exit(1); // Give up.
		}

		// Initialize information:
		int portNumber = Integer.parseInt(args[0]);

		// Initialises a ClientTable and a GameTable
		ClientTable clientTable = new ClientTable();
		GameTable gameTable = new GameTable();

		// Opens a server socket
		ServerSocket serverSocket = null;

		// Try to open the server socket on the given port
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.err.println("Couldn't listen on port " + portNumber);
			System.exit(1); // Give up.
		}

		try {
			// Server loops forever

			while (true) {
				// Listens to the socket, accepting connections from new clients
				Socket socket = serverSocket.accept();

				// This is so that readLine() can be used
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// Client gives a username/nickname to the server and the server reads it
				String clientName = fromClient.readLine();

				if (!clientTable.nickAlreadyInUse(clientName)) {
					// For debugging
					System.out.println(clientName + " connected");

					// Client is added to the CLientTable and the GameTable
					clientTable.add(clientName);
					gameTable.add(clientName);

					// Creates and starts a ServerReceiver thread
					(new ServerReceiver(clientName, fromClient, clientTable, gameTable)).start();

					// Creates a printstream and creates and starts a ServerSender thread
					PrintStream toClient = new PrintStream(socket.getOutputStream());
					(new ServerSender(clientTable.getQueue(clientName), toClient)).start();
				} else {
					System.out.println("Error: Nickname " + clientName + " is already in use.");
					socket.close();
					fromClient.close();
				}
			}
		} catch (IOException e) {
			System.err.println("IO error " + e.getMessage());
		}
	}
}
