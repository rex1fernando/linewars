package linewars.test;

import javax.swing.JFrame;

import linewars.display.panels.TechPanel;

public class TechPanelTest extends JFrame
{
	public static void main(String args[])
	{
		new TechPanelTest();
	}
	
	public TechPanelTest()
	{
		TechPanel panel = new TechPanel(null);
		add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
}
