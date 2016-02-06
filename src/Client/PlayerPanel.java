package Client;

import java.io.PrintStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import Server.ClientTable;
import Server.Server;

public class PlayerPanel extends JPanel {
	
	

	public PlayerPanel(String nickname, ClientSender sender, ClientReceiver receiver){
		super();
		
		JButton refresh = new JButton("Refresh");
		
		
		//setLayout(new BoxLayout(this, 0));
		
		refresh.addActionListener(e -> {
			
			sender.getServer().println("refresh:" + nickname);

			String players = receiver.getPlayerArray();
			System.out.println(players);
			
			String splitInput[] = players.split(":");
			
			for (int i = 0; i < splitInput.length; i++) {
			    JButton challenge = new JButton("Challenge");
			    add(challenge);
			}

		
		
		});
		
		

		//Create and populate the panel.

		
		//Lay out the panel.
		//PlayerPanel.makeCompactGrid(p,players.length, 2,6, 6,6, 6);       //xPad, yPad
		
		add(refresh);
	}

	
}
