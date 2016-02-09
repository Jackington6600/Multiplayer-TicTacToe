package Server;

public class Command {

	private final String command;

	// Constructor for a new command
	Command(String command) {
		this.command = command;
	}

	/**
	 * Returns the string of the command
	 * @return String The string of the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * toString method for a command
	 */
	public String toString() {
		return command;
	}

}
