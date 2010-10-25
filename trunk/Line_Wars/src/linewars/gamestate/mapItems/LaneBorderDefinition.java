package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollidesWithAll;
import linewars.gamestate.shapes.Circle;
import linewars.parser.Parser.InvalidConfigFileException;

public class LaneBorderDefinition extends MapItemDefinition {

	public LaneBorderDefinition(double size) throws FileNotFoundException, InvalidConfigFileException {
		super();
		body = new Circle(new Transformation(new Position(0, 0), 0), size);
		
		cStrat = new CollidesWithAll();
	}
	
	public LaneBorder createLaneBorder(Transformation t)
	{
		return new LaneBorder(t, this);
	}

}
