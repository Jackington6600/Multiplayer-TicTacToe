package Server;

import java.net.*;
import java.io.*;

// Continuously reads from message queue for a particular client,
// forwarding to the client.

public class ServerSender extends Thread {
	private CommandQueue queue;
	private PrintStream client;

	public ServerSender(CommandQueue q, PrintStream c) {
		queue = q;
		client = c;
	}

	public void run() {
		while (true) {
			Command c = queue.take();
			client.println(c);
		}
	}
}
