package Server;

import java.util.concurrent.*;

/**
 * ClientTable allows each user to have a separate queue of incoming commands
 * @author Jack Morrison
 */

public class ClientTable {

	private ConcurrentMap<String, CommandQueue> queueTable = new ConcurrentHashMap<String, CommandQueue>();

	// The same nickname cannot be used twice (due to the code in Server.java)

	/**
	 * Add a user to the ClientTable and assign them a key and a command queue
	 * @param nickname Adds a user to the ClientTable
	 */
	public void add(String nickname) {
		queueTable.put(nickname, new CommandQueue());
	}

	/**
	 * Returns the queue of a given user
	 * @param nickname The user
	 * @return CommandQueue The queue
	 */
	public CommandQueue getQueue(String nickname) {
		return queueTable.get(nickname);
	}

	/**
	 * Returns a string of all the players (keys) in the ClientTable
	 * @return String The players
	 */
	public String getPlayers() {
		String output = "players";
		for (ConcurrentMap.Entry<String, CommandQueue> e : queueTable.entrySet()) {
			String key = e.getKey();
			output = output + ":" + key;
		}
		return output;
	}

	/**
	 * Returns true if the nickname is already in the ClientTable
	 * @param nickname The user
	 * @return boolean Is the nickname already in use?
	 */
	public boolean nickAlreadyInUse(String nickname) {
		if (queueTable.containsKey(nickname))
			return true;
		else
			return false;
	}

	/**
	 * Removes the given user (nickname) from the ClientTable
	 * @param nickname The users nickname
	 */
	public void remove(String nickname) {
		queueTable.remove(nickname);
	}

}
