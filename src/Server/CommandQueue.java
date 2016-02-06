package Server;
// We use https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/BlockingQueue.html

import java.util.concurrent.*;

public class CommandQueue {

  // We choose the LinkedBlockingQueue implementation of BlockingQueue:
  private BlockingQueue<Command> queue = new LinkedBlockingQueue<Command>();

  // Inserts the specified message into this queue.
  public void offer(Command c) {
    queue.offer(c);
  }

  // Retrieves and removes the head of this queue, waiting if
  // necessary until an element becomes available.
  public Command take() {

    while (true) {
      try {
        return(queue.take());
      }
      catch (InterruptedException e) {
        // This can in principle be triggered by queue.take().
        // But this would only happen if we had interrupt() in our code,
        // which we don't.
        // In any case, if it could happen, we should do nothing here
        // and try again until we succeed without interruption.
      }

    }
  }
}
