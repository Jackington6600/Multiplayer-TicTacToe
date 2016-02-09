package Client;

import java.io.*;

/**
 * Reads input from the user
 * Then constructs commands that the server will then use
 * (This prevents users from using higher level commands such as starting a game with another
 * player without them accepting a challenge)
 * Also prevents invalid commands being sent to the server
 * @author Jack Morrison
 */

public class ClientSender extends Thread {

	private String nickname;
	private PrintStream server;
	private ClientReceiver receiver;

	/**
	 * ClientSender constructor
	 * @param nickname String The users nickname
	 * @param server Printstream The printstream to the server
	 * @param receiver ClientReceiver The ClientReceiver for the client
	 */
	ClientSender(String nickname, PrintStream server, ClientReceiver receiver) {
		this.nickname = nickname;
		this.server = server;
		this.receiver = receiver;
	}

	public void run() {
		// So that the method readLine can be used
		BufferedReader user = new BufferedReader(new InputStreamReader(System.in));

		try {
			// Tell the server what the users nickname is
			server.println(nickname);

			server.println("refresh:" + nickname);

			// Then loop that takes in the users input and processes it accordingly
			while (true) {

				String input = user.readLine();
				String splitInput[] = input.split(":");
				// When the user is challenged, wait for an input of y || n
				// Inputs other than y/n will not effect the challenge in any way
				// as there is no other was to set challenged to false
				if (receiver.isChallenged()) {
					if (input.equals("y")) {
						server.println("accept:" + receiver.getChallenger());
						receiver.setChallenged(false);
					} else if (input.equals("n")) {
						server.println("decline:" + receiver.getChallenger());
						receiver.setChallenged(false);
					}
				}
				// If the input is challenge and the user is not in a game
				else if (splitInput[0].equals("challenge") && !receiver.inGame()) {
					server.println(input);
				}
				// If the input is refresh and the user is not in a game
				else if (splitInput[0].equals("refresh") && !receiver.inGame()) {
					server.println(input);
				}
				// If the input is move and the user is in a game
				else if (splitInput[0].equals("move") && receiver.inGame()) {
					
					// Split into several if statements to allow for specific
					// error messages.
					
					// If it is the users turn
					if (receiver.isTurn()) {
						int i = Integer.parseInt(splitInput[1]);
						int j = Integer.parseInt(splitInput[2]);
						// If the move is valid
						if (i < 3 && i >= 0 && j < 3 && j >= 0 && receiver.isValidMove(i, j)) {
							server.println(splitInput[0] + ":" + receiver.inGameWith() + ":" + splitInput[1] + ":"
									+ splitInput[2]);
						} else {
							System.out.println("This is not a valid move.");
						}
					} else {
						System.out.println("It is not your turn.");
					}
				}
				// If the input is end and the user is in a game
				else if (splitInput[0].equals("end") && receiver.inGame()) {
					server.println("end:" + receiver.inGameWith());
				}
				// If the input is quit and the user is in a game
				// Used to end the game first, then quit
				else if (splitInput[0].equals("quit") && receiver.inGame()) {
					server.println("end:" + receiver.inGameWith());
					server.println("quit:" + nickname);
					// System.exit(0);
				}
				// If the input is quit and the user is not in a game
				else if (splitInput[0].equals("quit")) {
					System.out.println("Leaving server.");
					server.println("quit");
					// System.exit(0);
				}
				// else the command is invalid
				else {
					System.out.println("Invalid command.");
				}

			}
		} catch (IOException e) {
			System.err.println("Communication broke in ClientSender" + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Returns the printstream to the server
	 * @return PrintStream The printstream to the server
	 */
	public PrintStream getServer() {
		return server;
	}
}
