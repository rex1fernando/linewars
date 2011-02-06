package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.gamestate.GameState;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollidesWithAll;
import linewars.gamestate.shapes.Circle;

/**
 * 
 * @author Connor Schenck
 *
 */
public strictfp class LaneBorderDefinition extends MapItemDefinition<LaneBorder> {

	public LaneBorderDefinition(GameState gameState, double size) throws FileNotFoundException, InvalidConfigFileException {
		super(gameState);
		body = new Circle(new Transformation(new Position(0, 0), 0), size);
		
		cStrat = new CollidesWithAll();
	}
	
	public LaneBorder createLaneBorder(Transformation t)
	{
		return new LaneBorder(t, this);
	}

	@Override
	protected void forceSubclassReloadConfigData() {
				
	}

	@Override
	public LaneBorder createMapItem(Transformation t) {
		return new LaneBorder(t, this);
	}

}