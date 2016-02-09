package Client;

import java.io.*;
import java.net.*;

/**
 * Client joins a server with a given hostname and port, using a given username
 * Creates a ClientReceiver and a ClientSender 
 * @author Jack Morrison
 */

class Client {

	public static void main(String[] args) {

		// Check correct usage
		if (args.length != 3) {
			System.err.println("Usage: java Client user-nickname port hostname");
			System.exit(1); // Give up.
		}

		// Initialize information
		String nickname = args[0];
		int portNumber = Integer.parseInt(args[1]);
		String hostname = args[2];

		// Open sockets
		PrintStream toServer = null;
		BufferedReader fromServer = null;
		Socket server = null;

		// Create the Socket, PrintStream and BufferedReader
		try {
			server = new Socket(hostname, portNumber);
			toServer = new PrintStream(server.getOutputStream());
			fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + hostname);
			System.exit(1); // Give up.
		} catch (IOException e) {
			System.err.println("The server doesn't seem to be running " + e.getMessage());
			System.exit(1); // Give up.
		}

		// Create two client threads

		ClientReceiver receiver = new ClientReceiver(fromServer);
		ClientSender sender = new ClientSender(nickname, toServer, receiver);

		// Run them in parallel
		sender.start();
		receiver.start();

		// Wait for them to end and close sockets
		try {
			sender.join();
			toServer.close();
			receiver.join();
			fromServer.close();
			server.close();
		} catch (IOException e) {
			System.err.println("Something wrong " + e.getMessage());
			System.exit(1); // Give up.
		} catch (InterruptedException e) {
			System.err.println("Unexpected interruption " + e.getMessage());
			System.exit(1); // Give up.
		}
	}
}
