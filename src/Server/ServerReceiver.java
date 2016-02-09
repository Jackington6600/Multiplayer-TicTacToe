package Server;

import java.io.*;

/**
 * Gets messages from client and puts them in a queue, for another
 * thread to forward to the appropriate client.
 * @author Jack Morrison
 */

public class ServerReceiver extends Thread {
	private String myClientsName;
	private BufferedReader myClient;
	private ClientTable clientTable;
	private GameTable gameTable;

	/**
	 * Constructor for the ServerReceiver
	 * @param n String The nickname of the user
	 * @param c BufferedReader The buffered reader
	 * @param t ClientTable The client table
	 * @param g GameTable The game table
	 */
	public ServerReceiver(String n, BufferedReader c, ClientTable t, GameTable g) {
		myClientsName = n;
		myClient = c;
		clientTable = t;
		gameTable = g;
	}

	 
	public void run() {
		try {
			// Runs forever
			while (true) {
				
				// Waits for an input from the client using the buffered reader
				String input = myClient.readLine();
				// Splits the command into its constituent parts
				String splitInput[] = input.split(":");

				// If the input is quit: close the client and remove them from the client and game tables
				if (splitInput[0].equals("quit")) {
					myClient.close();
					clientTable.remove(myClientsName);
					gameTable.remove(myClientsName);
				}
				// If the input is challenge: Send the challenge to the given player
				else if (splitInput[0].equals("challenge")) {
					// If both users (challenger and challenged) are not in games currently
					if (!gameTable.inGame(myClientsName) && !gameTable.inGame(splitInput[1])) {
						Command ch = new Command(splitInput[0] + ":" + splitInput[1] + ":" + myClientsName);
						CommandQueue recipientsQueue = clientTable.getQueue(splitInput[1]);
						// If the user has not challenged themselves
						if (!splitInput[1].equals(myClientsName)) {
							if (recipientsQueue != null)
								recipientsQueue.offer(ch);
							else
								System.err.println("Challenge for unexistent client " + splitInput[1] + " from " + myClientsName);
						}
						else {
							System.err.println("User: " + myClientsName + " is trying to send a challenge to themselves. Maybe they are lonely...");
						}
					}
					else {
						System.err.println("Could not send challenge from: " + myClientsName + " to " + splitInput[1]);
						System.err.println("One or more of the users are already in a game.");

						CommandQueue failQueue = clientTable.getQueue(myClientsName);
						Command failRecipient = new Command("message:Could not send challenge to " + splitInput[1]);

						if (failQueue != null) {
							failQueue.offer(failRecipient);
						}
						else
							System.err.println("Could not send error message to: " + myClientsName);
					}
				}
				// If the input is refresh: get the players from the client table and send it to the user
				else if (splitInput[0].equals("refresh")) {

					Command refresh = new Command(clientTable.getPlayers());
					CommandQueue recipientsQueue = clientTable.getQueue(myClientsName);

					if (recipientsQueue != null)
						recipientsQueue.offer(refresh);
					else
						System.err.println("Unable to refresh playerlist for user: " + myClientsName);
				}
				// If the input is accept: send a startGame command to each client (challenger and acceptor)
				else if (splitInput[0].equals("accept")) {
					// If both users are not currently in games
					if (!gameTable.inGame(myClientsName) && !gameTable.inGame(splitInput[1])) {
						CommandQueue recipientsQueue = clientTable.getQueue(myClientsName);
						CommandQueue challengersQueue = clientTable.getQueue(splitInput[1]);
						Command gameRecipient = new Command("startGame:" + splitInput[1] + ":false:" + myClientsName);
						Command gameChallenger = new Command("startGame:" + myClientsName + ":true:" + splitInput[1]);
						if (recipientsQueue != null && challengersQueue != null) {
							recipientsQueue.offer(gameRecipient);
							challengersQueue.offer(gameChallenger);
						}
						else
							System.err.println(
									"Could not start game between: " + splitInput[1] + " and " + myClientsName);

						gameTable.setInGame(myClientsName, true);
						gameTable.setInGame(splitInput[1], true);
					}
					else {

						System.err.println("Could not start game between: " + splitInput[1] + " and " + myClientsName);
						System.err.println("One or more of the users are already in a game.");

						CommandQueue failQueue = clientTable.getQueue(myClientsName);
						Command failRecipient = new Command("message:Could not start a game with " + splitInput[1]);

						if (failQueue != null) {
							failQueue.offer(failRecipient);
						} else
							System.err.println("Could not send error message to: " + myClientsName);
					}
				}
				// If the input is decline: send a message to the challenger that their challenge was declined
				else if (splitInput[0].equals("decline")) {

					CommandQueue challengersQueue = clientTable.getQueue(splitInput[1]);
					Command declineChallenger = new Command("decline:" + myClientsName);
					if (challengersQueue != null) {
						challengersQueue.offer(declineChallenger);
					} else
						System.err
								.println("Could not decline game between: " + splitInput[1] + " and " + myClientsName);

				}
				// If the input is move: send the move to the correct player
				else if (splitInput[0].equals("move")) {

					Command move = new Command(splitInput[0] + ":" + splitInput[1] + ":" + myClientsName + ":"
							+ splitInput[2] + ":" + splitInput[3]);
					CommandQueue recipientsQueue = clientTable.getQueue(splitInput[1]);
					CommandQueue moveMakerQueue = clientTable.getQueue(myClientsName);
					// To stop a user sending a move to themselves
					if (!myClientsName.equals(splitInput[1])) {
						if (recipientsQueue != null) {
							recipientsQueue.offer(move);
							moveMakerQueue.offer(move);
						} else
							System.err.println("Unable to send move to user: " + splitInput[1]);
					} else {
						System.err.println("User: " + myClientsName + " is trying to send a move to themselves.");
					}
				}
				// If the input is end: send an endGame command to each client
				else if (splitInput[0].equals("end")) {

					CommandQueue recipientsQueue = clientTable.getQueue(myClientsName);
					CommandQueue challengersQueue = clientTable.getQueue(splitInput[1]);
					Command gameRecipient = new Command("endGame:" + splitInput[1] + ":" + myClientsName);
					Command gameChallenger = new Command("endGame:" + myClientsName + ":" + splitInput[1]);
					if (recipientsQueue != null && challengersQueue != null) {
						recipientsQueue.offer(gameRecipient);
						challengersQueue.offer(gameChallenger);
					} else
						System.err.println("Could not end game between: " + splitInput[1] + " and " + myClientsName);

					// Sets inGame to false for both clients in the GameTable
					gameTable.setInGame(myClientsName, false);
					gameTable.setInGame(splitInput[1], false);

				}

				else {
					myClient.close();
					return;
				}

			}
		} catch (IOException e) {
			System.err.println("Something went wrong with the client " + myClientsName + " " + e.getMessage());
			// No point in trying to close sockets. Just give up.
			// We end this thread (we don't do System.exit(1)).
		}
	}
}
