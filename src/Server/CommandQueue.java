package Server;

import java.util.concurrent.*;

/**
 * The CommandQueue to be used in the ClientTable for storing outgoing/incoming commands
 * @author Jack Morrison
 */

public class CommandQueue {

	// LinkedBlockingQueue implementation of BlockingQueue
	private BlockingQueue<Command> queue = new LinkedBlockingQueue<Command>();

	/**
	 * Offers a given command to the queue
	 * @param c The command to be offered to the queue
	 */
	public void offer(Command c) {
		queue.offer(c);
	}

	/**
	 * Takes and removes the command at the head of this queue
	 * Waits if there are no commands in the queue
	 * @return Command The command at the head of the queue
	 */
	public Command take() {
		while (true) {
			try {
				return (queue.take());
			} catch (InterruptedException e) {
				// This can in principle be triggered by queue.take().
				// But this would only happen if we had interrupt() in our code,
				// which we don't.
				// In any case, if it could happen, we should do nothing here
				// and try again until we succeed without interruption.
			}
		}
	}
}
