package Server;
// Each nickname has a different incomming-message queue.

import java.util.Set;
import java.util.concurrent.*;

public class ClientTable {

	private ConcurrentMap<String, CommandQueue> queueTable = new ConcurrentHashMap<String, CommandQueue>();

	// The following overrides any previously existing nickname, and
	// hence the last client to use this nickname will get the messages
	// for that nickname, and the previously exisiting clients with that
	// nickname won't be able to get messages. Obviously, this is not a
	// good design of a messaging system. So I don't get full marks:

	public void add(String nickname) {
		queueTable.put(nickname, new CommandQueue());
	}

	// Returns null if the nickname is not in the table:
	public CommandQueue getQueue(String nickname) {
		return queueTable.get(nickname);
	}

	public String getPlayers() {
		String output = "players";
	  for (ConcurrentMap.Entry<String, CommandQueue> e : queueTable.entrySet()) {
		    String key = e.getKey();
		    output = output + ":" + key;
	  }
	  return output;
  }
	
	public boolean nickAlreadyInUse(String nickname) {
		if (queueTable.containsKey(nickname))
			return true;
		else
			return false;
	}

}
