package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollidesWithAll;
import linewars.gamestate.shapes.Circle;
import linewars.parser.Parser.InvalidConfigFileException;

public class LaneBorderDefinition extends MapItemDefinition {

	public LaneBorderDefinition(double size) throws FileNotFoundException, InvalidConfigFileException {
		super();
		//TODO set the radius of the circle
		body = new Circle();
		
		cStrat = new CollidesWithAll();
	}
	
	public LaneBorder createLaneBorder(Transformation t)
	{
		return new LaneBorder(t, this);
	}

}
