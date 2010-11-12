package linewars.init;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.test.ConnorTest;

import editor.BigFrameworkGuy;


public class DrWeissRunMe {

	/**
	 * @param args
	 * @throws InvalidConfigFileException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, InvalidConfigFileException {
		Object[] options = {"Run the Family Member Generator?",
                "Run the Game?",
                "Quit?"};
		int n = JOptionPane.showOptionDialog(null,
		"Would you like to ",
		"Line Wars Startup",
		JOptionPane.YES_NO_CANCEL_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		options,
		options[2]);
		
		if(n == JOptionPane.YES_OPTION)
		{
			new BigFrameworkGuy();
		}
		else if(n == JOptionPane.NO_OPTION)
		{
			options = new String[]{"Yes", "No"};
			n = JOptionPane.showOptionDialog(null,
			"Would you like to run with networking?\n" +
			"(this will allow you to  play with players on other computers,\n" +
			"but will require that you know the IP addresses of the other computers)",
			"Line Wars Startup",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[1]);
			
			boolean networking = false;
			if(n == JOptionPane.YES_OPTION)
				networking = true;
			
			options = new String[]{"1", "2"};
			n = JOptionPane.showOptionDialog(null,
			"How many players?\n" +
			"(for debugging purposes you will have control of all players)",
			"Line Wars Startup",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]);
			
			int players = 1;
			if(n == JOptionPane.NO_OPTION)
				players = 2;
			
			String server = null;
			ArrayList<String> playerIPs = new ArrayList<String>();
			if(networking)
			{
				server = (String)JOptionPane.showInputDialog(
	                    null,
	                    "What is the server IP? (if hosting locally type \"127.0.0.1\")");
				
				for(int i = 1; i <= players; i++)
					playerIPs.add((String)JOptionPane.showInputDialog(
	                    null,
	                    "What is Player " + i + "\'s  IP? (if this is the local player, type \"127.0.0.1\")"));
			}
			else
				for(int i = 0; i < players; i++)
					playerIPs.add("127.0.0.1");
			
			ArrayList<String> playerNames = new ArrayList<String>();
			ArrayList<String> playerRaces = new ArrayList<String>();
			
			for(int i = 1; i <= players; i++)
			{
				playerNames.add((String)JOptionPane.showInputDialog(
	                    null,
	                    "What is Player " + i + "\'s  name?"));
				playerRaces.add("resources/races/thatOneRace.cfg");
			}
			
			
			
			JOptionPane.showMessageDialog(null,
				    "We currently have two maps to show you:\n" +
				    "1: A simple 2-nodes-connected-by-one-lane map.\n" +
				    "2: Uses the same map image as 1 but adds two new\n" +
				    "   nodes: above and below the lane. As well adds\n" +
				    "   lanes connecting the new nodes.",
				    "Line Wars Startup", JOptionPane.DEFAULT_OPTION);
			
			options = new String[]{"1", "2"};
			n = JOptionPane.showOptionDialog(null,
			"Which map would you like to play?",
			"Line Wars Startup",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]);
			
			String mapURI = "resources/maps/map1.cfg";
			if(n == JOptionPane.NO_OPTION)
				mapURI = "resources/maps/4_node_map.cfg";
			
			options = new String[]{"Yes", "No, hit it!"};
			n = JOptionPane.showOptionDialog(null,
			"Would you like a brief tutorial?",
			"Line Wars Startup",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]);
			
			if(n == JOptionPane.YES_OPTION)
			{
				JOptionPane.showMessageDialog(null,
					    "\tYou will start the game in tactical view. This\n" +
					    "will let you see the individual units, buildings,\n" +
					    "etc. If you select the command center in the center\n" +
					    "of a node, the command card panel (bottom right) will\n" +
					    "appear. Hovering over the buttons will tell you what\n" +
					    "each one does.\n" +
					    "\t Putting your mouse near the edge of the screen will\n" +
					    "cause your view to pan around the map. Using the scroll\n" +
					    "wheel on your mouse will cause your view to zoom in or\n" +
					    "our. If you zoom out enough, your view will switch to\n" +
					    "strategic view. This will give you an overview of what\n" +
					    "is happening on the map. Dragging the red circles on\n" +
					    "each node will adjust the flow of units coming out of\n" +
					    "that node. Zooming back in will return you to tactical\n" +
					    "view.\n" +
					    "\tTo exit the game, click the exit button in the upper left",
					    "Line Wars Tutorial", JOptionPane.DEFAULT_OPTION);
			}
			
			if(networking)
			{
				Game toStart = new Game(mapURI, players, server, playerRaces, playerRaces, playerIPs);
				toStart.initialize();
				toStart.run();
			}
			else
			{
				ConnorTest toStart = new ConnorTest(mapURI, players, server, playerRaces, playerRaces, playerIPs);
				toStart.initialize();
				toStart.run();
			}
		}
	}

}
