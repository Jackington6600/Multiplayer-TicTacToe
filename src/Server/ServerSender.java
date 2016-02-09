package Server;

import java.io.*;

/**
 * Continuously reads from message queue for a particular client,
 * forwarding to the client.
 * @author Jack Morrison
 */

public class ServerSender extends Thread {
	private CommandQueue queue;
	private PrintStream client;

	/**
	 * Constructor for the ServerSender
	 * @param q CommandQueue The command queue of a client
	 * @param c PrintStream The print stream of a client
	 */
	public ServerSender(CommandQueue q, PrintStream c) {
		queue = q;
		client = c;
	}

	// Runs forever
	public void run() {
		while (true) {
			Command c = queue.take();
			client.println(c);
		}
	}
}
