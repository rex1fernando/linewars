package editor.mapEditor;

import java.awt.Dimension;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import linewars.configfilehandler.ConfigFileReader;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;

public class MapFrame extends JFrame
{
	public static void main(String args[])
	{
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}

    private static void createAndShowGUI()
    {
        //Create and set up the window.
        JFrame frame = new JFrame("MapEditor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add the ubiquitous "Hello World" label.
        MapEditor editor = new MapEditor(1024, 768);
        frame.setContentPane(editor);
        frame.setSize(new Dimension(1024, 768));
        
        try
		{
			ConfigFileReader reader = new ConfigFileReader("resources/maps/map1.cfg");
			editor.setData(reader.read());
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidConfigFileException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

        //Display the window.
        // frame.pack();
        frame.setVisible(true);
    }
}
