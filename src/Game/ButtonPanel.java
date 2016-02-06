package Game;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.*;

public class ButtonPanel extends JPanel
{

	public ButtonPanel()
	{
		super();
		
		JButton exit = new JButton("Exit");
		exit.addActionListener(e -> System.exit(0));
		
		add(exit);
	}
}
