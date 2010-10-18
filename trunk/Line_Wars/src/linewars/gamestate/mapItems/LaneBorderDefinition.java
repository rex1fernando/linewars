package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollidesWithAll;
import linewars.parser.Parser.InvalidConfigFileException;

public class LaneBorderDefinition extends MapItemDefinition {

	public LaneBorderDefinition() throws FileNotFoundException, InvalidConfigFileException {
		super();
		//TODO set default body for lane borders
		body = null;
		
		cStrat = new CollidesWithAll();
	}
	
	public LaneBorder createLaneBorder(Transformation t)
	{
		return new LaneBorder(t, this);
	}

}
