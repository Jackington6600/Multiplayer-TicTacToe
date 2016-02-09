package Server;

import java.util.concurrent.*;

/**
 * GameTable stores a ConcurrentHashMap of all current players alongside a boolean
 * The boolean states whether or not the user is currently in a game
 * @author Jack Morrison
 */

public class GameTable {

	private ConcurrentMap<String, Boolean> gameTable = new ConcurrentHashMap<String, Boolean>();


	/**
	 * Adds a given user (nickname) to the GameTable
	 * @param nickname The user
	 */
	public void add(String nickname) {
		gameTable.put(nickname, false);
	}

	/**
	 * Returns true if the given user (nickname) is in a game currently
	 * @param nickname The user
	 * @return boolean Is the user in a game
	 */
	public boolean inGame(String nickname) {
		return gameTable.get(nickname);
	}

	/**
	 * Sets whether a user is in a game or not
	 * @param nickname The user
	 * @param b boolean Is the user in a game currently?
	 */
	public void setInGame(String nickname, boolean b) {
		gameTable.replace(nickname, gameTable.get(nickname), b);
	}

	/**
	 * Removes the given user from the GameTable
	 * @param nickname The user
	 */
	public void remove(String nickname) {
		gameTable.remove(nickname);
	}

}
