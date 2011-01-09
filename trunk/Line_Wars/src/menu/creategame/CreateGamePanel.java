package menu.creategame;

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import menu.WindowManager;

public class CreateGamePanel extends JPanel
{
	private WindowManager parent;
	
	private NamePanel namePanel;
	
	public CreateGamePanel(WindowManager parent)
	{
		this.parent = parent;
		setSize(parent.getPanelSize());
		
		GridLayout mgr = new GridLayout(3, 1);
		setLayout(mgr);
		
		setOpaque(false);
		initComponents();
	}
	
	private void initComponents()
	{
		namePanel = new NamePanel();
		add(namePanel);
		
		initReplayAndMap();
	}
	
	private void initReplayAndMap()
	{
		
	}
	
	private class NamePanel extends JPanel
	{
		private JLabel nameLabel;
		private JTextField nameField;
		
		public NamePanel()
		{
			nameLabel = new JLabel("Name");
			nameField = new JTextField("Jephthah");
		}
	}
	
	private class ReplayBox extends JPanel
	{
		private JLabel replayLabel;
		private JCheckBox replayBox;
		
		public ReplayBox()
		{
			replayLabel = new JLabel("Replay");
			replayBox = new JCheckBox();
		}
	}
}
