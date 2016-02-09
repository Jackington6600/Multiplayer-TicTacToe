package Client;

import java.io.*;
import Game.NoughtsCrosses;
import Game.NoughtsCrossesModel;

/**
 * ClientReceiver is a thread that gets commands from the server and processes them based on input.
 * @author Jack Morrison
 */

public class ClientReceiver extends Thread {

	private BufferedReader server;
	private String players;
	private boolean challenged;
	private boolean inGame;
	private boolean isTurn;
	private String inGameWith;
	private String challenger;
	private NoughtsCrosses game;
	private NoughtsCrossesModel gameModel;

	ClientReceiver(BufferedReader server) {
		this.server = server;
	}
	
	public void run() {
		try {
			while (true) {
				// Read the commands (strings) sent from the server
				String s = server.readLine();
				if (s != null) {
					// Split the strings into parts and into an array
					String splitInput[] = s.split(":");
					
					// If the command is a challenge and the user is not in game
					if (splitInput[0].equals("challenge") && !inGame) {

						System.out.println("Challenge from: " + splitInput[2]);
						System.out.println("Do you accept? (y/n)");
						challenger = splitInput[2];
						challenged = true;
						
					} 
					//if the command is players, print out the players
					else if (splitInput[0].equals("players")) {

						players = s;
						System.out.println(players);

					}
					/*
					 * If the command is startGame then:
					 * sets inGame to true
					 * sets inGameWith to the opponent
					 * creates a NoughtsCrosses game
					 */ 
					else if (splitInput[0].equals("startGame")) {
						inGame = true;
						System.out.println("Now in game with " + splitInput[1]);
						inGameWith = splitInput[1];

						game = new NoughtsCrosses();
						gameModel = new NoughtsCrossesModel(game);
						// To ensure a new game is created
						gameModel.newGame();

						// Sets isTurn to true or false (deciding initially who's turn it is is done by ServerReceiver
						// Which is sent to each client as a part of the startGame command)
						if (splitInput[2].equals("true")) {
							isTurn = true;
							System.out.println("It is your turn.");
						}
						else {
							isTurn = false;
							System.out.println("It is not your turn.");
						}
						
						// Print out the game board
						for (int i = 0; i < 3; i++) {
							System.out.print(gameModel.getString(i, 0));
							System.out.print(gameModel.getString(i, 1));
							System.out.print(gameModel.getString(i, 2));
							System.out.println("");
						}

						System.out.println("");

						// Loop for while the game is ongoing
						while (inGame) {

							String move = server.readLine();
							String splitMove[] = move.split(":");
							if (move != null) {
								// If the move is from the opponent
								// Sets isTurn to true, because the opponent has made their move, so you can now make one
								if (splitMove[0].equals("move") && inGameWith.equals(splitMove[2])) {
									System.out.println(move);
									gameModel.turn(Integer.parseInt(splitMove[3]), Integer.parseInt(splitMove[4]));
									if (gameModel.whoWon() == 1) {
										System.out.println(splitMove[2] + " (Crosses) wins the game!");
										gameModel.newGame();
									} 
									else if (gameModel.whoWon() == 2) {
										System.out.println(splitMove[2] + " (Noughts) wins the game!");
										gameModel.newGame();
									}
									for (int i = 0; i < 3; i++) {
										System.out.print(gameModel.getString(i, 0));
										System.out.print(gameModel.getString(i, 1));
										System.out.print(gameModel.getString(i, 2));
										System.out.println("");
									}
									isTurn = true;
								}
								// If the move is from yourself
								// Sets isTurn to false, because you have just made your turn and you cannot make another
								// Once a game ends and a player wins, each client will get a message saying who has won
								// followed by a new game being started
								else if (splitMove[0].equals("move") && splitInput[3].equals(splitMove[2])) {
									System.out.println(move);
									gameModel.turn(Integer.parseInt(splitMove[3]), Integer.parseInt(splitMove[4]));
									if (gameModel.whoWon() == 1) {
										System.out.println(splitMove[2] + " (Crosses) wins the game!");
										gameModel.newGame();
									} else if (gameModel.whoWon() == 2) {
										System.out.println(splitMove[2] + " (Noughts) wins the game!");
										gameModel.newGame();
									}
									for (int i = 0; i < 3; i++) {
										System.out.print(gameModel.getString(i, 0));
										System.out.print(gameModel.getString(i, 1));
										System.out.print(gameModel.getString(i, 2));
										System.out.println("");
									}
									isTurn = false;
								}
								// If the command is endGame then set inGame to false
								// This is the only way to end a game
								else if (splitMove[0].equals("endGame") && splitInput[1].equals(splitMove[1])) {
									System.out.println("No longer in game with " + inGameWith);
									inGame = false;
								}
							}
						}
					}
					// If the command is decline then the opponent has declined your challenge
					else if (splitInput[0].equals("decline")) {
						System.out.println(splitInput[1] + " declined your challenge.");
					}
					// If the command is message then print the message
					// This is used for error messages being sent to clients
					else if (splitInput[0].equals("message")) {
						System.out.println(splitInput[1]);
					}
				}
				else {
					server.close(); // Probably no point.
					throw new IOException("Got null from server");
				}

			}
		} catch (IOException e) {
			System.out.println("Server seems to have died " + e.getMessage());
			System.exit(1); // Give up.
		}
	}

	/**
	 * Returns the string of current players
	 * @return String String of current players
	 */
	public String getPlayerArray() {
		return players;
	}

	/**
	 * Returns true if the player has been challenged
	 * @return boolean Has the player been challenged?
	 */
	public boolean isChallenged() {
		return challenged;
	}

	/**
	 * Sets whether the user has been challenged
	 * @param b boolean Sets challenged to true or false based on input
	 */
	public void setChallenged(boolean b) {
		challenged = b;
	}

	/**
	 * Gets the name of the challenger
	 * @return String Challenger name
	 */
	public String getChallenger() {
		return challenger;
	}

	/**
	 * Returns true if the player is in a game
	 * @return boolean Is the player in a game?
	 */
	public boolean inGame() {
		return inGame;
	}

	/**
	 * Returns true if it's the players turn
	 * @return boolean Is it the players turn
	 */
	public boolean isTurn() {
		return isTurn;
	}

	/**
	 * Returns true if the move trying to be made is valid
	 * @param i the i coordinate
	 * @param j the j coordinate
	 * @return boolean Is the move trying to be made valid?
	 */
	public boolean isValidMove(int i, int j) {
		if (gameModel.get(i, j) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the name of the of the players opponent
	 * @return String Name of the opponent
	 */
	public String inGameWith() {
		return inGameWith;
	}
}
