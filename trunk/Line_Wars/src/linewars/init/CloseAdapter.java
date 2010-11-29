package linewars.init;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class CloseAdapter extends WindowAdapter
{
	private JFrame main;
	
	public CloseAdapter(JFrame main)
	{
		this.main = main;
	}
	
	@Override
	public void windowClosing(WindowEvent e)
	{
		main.dispose();
	}
}
