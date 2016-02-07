package Server;
// Each nickname has a different incomming-message queue.

import java.util.Set;
import java.util.concurrent.*;

public class GameTable {

	private ConcurrentMap<String, Boolean> gameTable = new ConcurrentHashMap<String, Boolean>();

	// The following overrides any previously existing nickname, and
	// hence the last client to use this nickname will get the messages
	// for that nickname, and the previously exisiting clients with that
	// nickname won't be able to get messages. Obviously, this is not a
	// good design of a messaging system. So I don't get full marks:

	public void add(String nickname) {
		gameTable.put(nickname, false);
	}

	// Returns null if the nickname is not in the table:
	public boolean inGame(String nickname) {
		return gameTable.get(nickname);
	}
	
	public void setInGame(String nickname, boolean b) {
		gameTable.replace(nickname, gameTable.get(nickname), b);
	}

	

}
