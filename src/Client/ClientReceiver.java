package Client;

import java.io.*;
import java.net.*;

import javax.swing.JFrame;

import Game.NoughtsCrosses;
import Game.NoughtsCrossesComponent;
import Game.NoughtsCrossesModel;
import Server.Command;
import Server.CommandQueue;

// Gets messages from other clients via the server (by the
// ServerSender thread).

public class ClientReceiver extends Thread {

	private BufferedReader server;
	private String players;
	private boolean challenged;
	private boolean inGame;
	private boolean isTurn;
	private String challenger;
	private NoughtsCrosses game;
	private NoughtsCrossesModel gameModel;

	ClientReceiver(BufferedReader server) {
		this.server = server;
	}

	public void run() {
		// Print to the user whatever we get from the server:
		try {
			while (true) {
				String s = server.readLine();
				if (s != null) {
					
					String splitInput[] = s.split(":");
					if (splitInput[0].equals("challenge") && !inGame) {
						
						System.out.println("Challenge from: " + splitInput[2]);
						System.out.println("Do you accept? (y/n)");
						challenger = splitInput[2];
						challenged = true;
						
						
					}
					else if (splitInput[0].equals("players")) {
						
						//System.out.println(s);
						//System.out.println(splitInput.length);
						
						players = s;
						
						System.out.println(players);
						
						/*
						for(int i = 0; i < splitInput.length - 1; i++) {
							players[i] = splitInput[i];
							System.out.println(players[i]);
						}
						*/
					}
					else if (splitInput[0].equals("startGame")) {
						inGame = true;
						System.out.println("Now in game with " + splitInput[1]);
						
						game = new NoughtsCrosses();
						gameModel = new NoughtsCrossesModel(game);
						//to ensure a new game is created
						gameModel.newGame();
						
						if (splitInput[2].equals("true")) {
							isTurn = true;
							System.out.println("It is your turn.");
						}
						else {
							isTurn = false;
							System.out.println("It is not your turn.");
						}
						
						for (int i = 0; i < 3; i++) {
							System.out.print(gameModel.getString(i, 0));
							System.out.print(gameModel.getString(i, 1));
							System.out.print(gameModel.getString(i, 2));
							System.out.println("");
						}
						
						System.out.println("");
						
						while(inGame) {
							
							String move = server.readLine();
							String splitMove[] = move.split(":");
							if (move != null) {
								//If the move is from the opponent
								if (splitMove[0].equals("move") && splitInput[1].equals(splitMove[2])) {
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
								//If the move is from yourself
								else if (splitMove[0].equals("move") && splitInput[3].equals(splitMove[2])) {
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
									isTurn = false;
								}
								else if (splitMove[0].equals("endGame") && splitInput[1].equals(splitMove[1])) {
									System.out.println("No longer in game with " + splitInput[1]);
									inGame = false;
								}
							}
						}
					}
					else if (splitInput[0].equals("decline")) {
						System.out.println(splitInput[1] + " declined your challenge.");
					}
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
	
	public String getPlayerArray() {
		return players;
	}
	
	public boolean isChallenged() {
		return challenged;
	}
	
	public void setChallenged(boolean b) {
		challenged = b;
	}
	
	public String getChallenger() {
		return challenger;
	}
	
	public boolean inGame() {
		return inGame;
	}
	
	public boolean isTurn() {
		return isTurn;
	}
	
	public boolean isValidMove(int i, int j) {
		if (gameModel.get(i, j) == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
