package linewars.init;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * A window adapter that upon closing of a window disposes of the
 * GUI elements that compose that window.
 * 
 * @author Titus Klinge
 */
public class CloseAdapter extends WindowAdapter
{
	/**
	 * The window the close adapter should dispose of when called.
	 */
	private JFrame main;
	
	/**
	 * Creates a new CloseAdapter object with a JFrame to dispose of
	 * when called.
	 * 
	 * @param main The main window that will be closed and should be disposed
	 *             of at the right itme.
	 */
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
