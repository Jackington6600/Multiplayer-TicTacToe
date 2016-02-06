package Server;

public class Command {

	private final String command;
	
	Command(String command) {
		this.command = command;
	}
	
	public String getCommand() {
		return command;
	}

	public String toString() {
		return command;
	}
	
}
