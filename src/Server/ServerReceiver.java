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

	public ServerReceiver(String n, BufferedReader c, ClientTable t) {
		myClientsName = n;
		myClient = c;
		clientTable = t;
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
					Command ch = new Command(splitInput[0] + ":" + splitInput[1] + ":" + myClientsName);
					CommandQueue recipientsQueue = clientTable.getQueue(splitInput[1]);
					if (recipientsQueue != null)
						recipientsQueue.offer(ch);
					else
						System.err.println("Challenge for unexistent client " + splitInput[1] + " from " + myClientsName);
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
					
					Command move = new Command(clientTable.getPlayers());
					CommandQueue recipientsQueue = clientTable.getQueue(splitInput[1]);
					
					if (recipientsQueue != null) {
						recipientsQueue.offer(move);
						System.out.println("Should be sending move");
					}
						
					else
						System.err.println("Unable to send move to user: " + splitInput[1]);
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
