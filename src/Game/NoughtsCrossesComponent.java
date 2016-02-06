package Game;
import javax.swing.JPanel;

import Client.ClientReceiver;
import Client.ClientSender;
import Client.PlayerPanel;

import java.awt.BorderLayout;
import java.io.PrintStream;

public class NoughtsCrossesComponent extends JPanel
{
	public NoughtsCrossesComponent(String nickname, ClientSender sender, ClientReceiver receiver)
	{
		super();
		//NoughtsCrossesModel model = new NoughtsCrossesModel(game);
		
		//BoardView board = new BoardView(model);
		ButtonPanel buttons = new ButtonPanel();
		PlayerPanel players = new PlayerPanel(nickname, sender, receiver);
		
		//model.addObserver(board);
		
		setLayout(new BorderLayout());
		
		add(players, BorderLayout.WEST);
		//add(board, BorderLayout.CENTER);
		add(buttons, BorderLayout.SOUTH);
	}
}
