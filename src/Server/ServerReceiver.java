package Server;

import java.net.*;
import java.util.Map;
import java.io.*;

// Gets messages from client and puts them in a queue, for another
// thread to forward to the appropriate client.

public class ServerReceiver extends Thread {
	private String myClientsName;
	private BufferedReader myClient;
	private ClientTable clientTable;
	private GameTable gameTable;

	public ServerReceiver(String n, BufferedReader c, ClientTable t, GameTable g) {
		myClientsName = n;
		myClient = c;
		clientTable = t;
		gameTable = g;
	}

	public void run() {
		try {
			while (true) {
				/*
				String recipient = myClient.readLine();
				String text = myClient.readLine();
				if (recipient != null && text != null) {
					Message msg = new Message(myClientsName, text);
					MessageQueue recipientsQueue = clientTable.getQueue(recipient);
					if (recipientsQueue != null)
						recipientsQueue.offer(msg);
					else
						System.err.println("Message for unexistent client " + recipient + ": " + text);
				} else {
					myClient.close();
					return;
				}
				*/
				
				String input = myClient.readLine();
				String splitInput[] = input.split(":");
				if (splitInput[0].equals("challenge")) {
					if (!gameTable.inGame(myClientsName) && !gameTable.inGame(splitInput[1]))
					{
						Command ch = new Command(splitInput[0] + ":" + splitInput[1] + ":" + myClientsName);
						CommandQueue recipientsQueue = clientTable.getQueue(splitInput[1]);
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
						
						if (failQueue != null)
						{
							failQueue.offer(failRecipient);
						}
						else
							System.err.println("Could not send error message to: " + myClientsName);
					}
				}
				else if (splitInput[0].equals("refresh")) {
					
					Command refresh = new Command(clientTable.getPlayers());
					CommandQueue recipientsQueue = clientTable.getQueue(myClientsName);
					
					if (recipientsQueue != null)
						recipientsQueue.offer(refresh);
					else
						System.err.println("Unable to refresh playerlist for user: " + myClientsName);
				}
				else if (splitInput[0].equals("accept")) {
					if (!gameTable.inGame(myClientsName) && !gameTable.inGame(splitInput[1]))
					{
						CommandQueue recipientsQueue = clientTable.getQueue(myClientsName);
						CommandQueue challengersQueue = clientTable.getQueue(splitInput[1]);
						Command gameRecipient = new Command("startGame:" + splitInput[1]);
						Command gameChallenger = new Command("startGame:" + myClientsName);
						if (recipientsQueue != null && challengersQueue != null)
						{
							recipientsQueue.offer(gameRecipient);
							challengersQueue.offer(gameChallenger);
						}
						else
							System.err.println("Could not start game between: " + splitInput[1] + " and " + myClientsName);
						
						gameTable.setInGame(myClientsName, true);
						gameTable.setInGame(splitInput[1], true);
					}
					else {
						
						System.err.println("Could not start game between: " + splitInput[1] + " and " + myClientsName);
						System.err.println("One or more of the users are already in a game.");
						
						CommandQueue failQueue = clientTable.getQueue(myClientsName);
						Command failRecipient = new Command("message:Could not start a game with " + splitInput[1]);
						
						if (failQueue != null)
						{
							failQueue.offer(failRecipient);
						}
						else
							System.err.println("Could not send error message to: " + myClientsName);
					}
				}
				else if (splitInput[0].equals("decline")) {
					
					CommandQueue challengersQueue = clientTable.getQueue(splitInput[1]);
					Command declineChallenger = new Command("decline:" + myClientsName);
					if (challengersQueue != null)
					{
						challengersQueue.offer(declineChallenger);
					}
					else
						System.err.println("Could not decline game between: " + splitInput[1] + " and " + myClientsName);
					
				}
				
				else if (splitInput[0].equals("move")) {
					
					Command move = new Command(splitInput[0] + splitInput[1] + myClientsName + splitInput[2] + splitInput[3]);
					CommandQueue recipientsQueue = clientTable.getQueue(splitInput[1]);
					if (!myClientsName.equals(splitInput[1])) {
						if (recipientsQueue != null) {
							recipientsQueue.offer(move);
						}
						else
							System.err.println("Unable to send move to user: " + splitInput[1]);
					}
					else {
						System.err.println("User: " + myClientsName + " is trying to send a move to themselves.");
					}
				}
				
				else if (splitInput[0].equals("end")) {
					
					CommandQueue recipientsQueue = clientTable.getQueue(myClientsName);
					CommandQueue challengersQueue = clientTable.getQueue(splitInput[1]);
					Command gameRecipient = new Command("endGame:" + splitInput[1] + ":" + myClientsName);
					Command gameChallenger = new Command("endGame:" + myClientsName + ":" + splitInput[1]);
					if (recipientsQueue != null && challengersQueue != null)
					{
						recipientsQueue.offer(gameRecipient);
						challengersQueue.offer(gameChallenger);
						//System.err.println("Should end game between: " + splitInput[1] + " and " + myClientsName);
					}
					else
						System.err.println("Could not end game between: " + splitInput[1] + " and " + myClientsName);
					
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
