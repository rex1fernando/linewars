package linewars.test;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JFrame;

import linewars.display.Animation;
import linewars.display.panels.TechPanel;

public class TechPanelTest extends JFrame
{
	public static void main(String args[])
	{
		new TechPanelTest();
	}
	
	public TechPanelTest()
	{
		Animation techPanelBackground = null;
		try {
			techPanelBackground = (Animation)new ObjectInputStream(new FileInputStream(new File("resources/animations/tech_panel.cfg"))).readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		TechPanel panel = new TechPanel(null);
		panel.setParent(this);
		add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(720, 480));
		setVisible(true);
	}
}
