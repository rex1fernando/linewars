package linewars.init;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import editor.URISelector;

public class GameInitializer
{
	public static void main(String[] args)
	{
		JFrame f = new JFrame("Line Wars");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		InitPane pane = new InitPane();
		f.setContentPane(pane);
		
		f.pack();
		f.setVisible(true);
	}
	
	private static class InitPane extends JPanel
	{
		private ClientPanel client;
		private HostPanel host;
		
		public InitPane()
		{
			
		}
	}
	
	private static class ClientPanel extends JPanel
	{
		private JTextField nameField;
		private JLabel nameLabel;
		private JTextField serverField;
		private JLabel serverLabel;
		private URISelector raceSelector;
		
		public ClientPanel()
		{
			super(new GridLayout());
		}
	}
	
	private static class HostPanel extends JPanel
	{
		public HostPanel()
		{
			
		}
	}
}
