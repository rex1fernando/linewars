package linewars.gamestate;

import java.io.FileNotFoundException;

import linewars.parser.ConfigFile;
import linewars.parser.Parser;
import linewars.parser.Parser.InvalidConfigFileException;

public class GameStateManager
{
	// TODO Finish implementation
	
	private GameState displayState;
	
	public GameStateManager()
	{
		Parser mapParser = null;
		try
		{
			mapParser = new Parser(new ConfigFile("resources/display/map.cfg"));
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
		displayState = new GameState(mapParser);
	}
	
	public GameState getDisplayGameState()
	{
		return displayState;
	}
}
