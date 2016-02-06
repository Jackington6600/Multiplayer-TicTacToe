package Client;
// Usage:
//        java Client user-nickname hostname
//
// After initializing and opening appropriate sockets, we start two
// client threads, one to send messages, and another one to get
// messages.
//
// A limitation of our implementation is that there is no provision
// for a client to end after we start it. However, we implemented
// things so that pressing ctrl-c will cause the client to end
// gracefully without causing the server to fail.
//
// Another limitation is that there is no provision to terminate when
// the server dies.


import java.io.*;
import java.net.*;

import javax.swing.JFrame;

import Game.NoughtsCrosses;
import Game.NoughtsCrossesComponent;
import Server.Port;

class Client {

  public static void main(String[] args) {

    // Check correct usage:
    if (args.length != 2) {
      System.err.println("Usage: java Client user-nickname hostname");
      System.exit(1); // Give up.
    }

    // Initialize information:
    String nickname = args[0];
    String hostname = args[1];

    // Open sockets:
    PrintStream toServer = null;
    BufferedReader fromServer = null;
    Socket server = null;

    try {
      server = new Socket(hostname, Port.number);
      toServer = new PrintStream(server.getOutputStream());
      fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
    } 
    catch (UnknownHostException e) {
      System.err.println("Unknown host: " + hostname);
      System.exit(1); // Give up.
    } 
    catch (IOException e) {
      System.err.println("The server doesn't seem to be running " + e.getMessage());
      System.exit(1); // Give up.
    }

    // Create two client threads:
    
    ClientReceiver receiver = new ClientReceiver(fromServer);
    ClientSender sender = new ClientSender(nickname,toServer,receiver);
    

    // Run them in parallel:
    sender.start();
    receiver.start();
    
  	//GUI
  	NoughtsCrosses game = new NoughtsCrosses();
  	
  	NoughtsCrossesComponent comp = new NoughtsCrossesComponent(nickname,sender,receiver);
  	
  	JFrame frame = new JFrame("Noughts and Crosses");
  	frame.setSize(400, 400);
  	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  	
  	frame.add(comp);
  	
  	//frame.setVisible(true);
    
    // Wait for them to end and close sockets.
    try {
      sender.join();
      toServer.close();
      receiver.join();
      fromServer.close();
      server.close();
    }
    catch (IOException e) {
      System.err.println("Something wrong " + e.getMessage());
      System.exit(1); // Give up.
    }
    catch (InterruptedException e) {
      System.err.println("Unexpected interruption " + e.getMessage());
      System.exit(1); // Give up.
    }
  }
}
