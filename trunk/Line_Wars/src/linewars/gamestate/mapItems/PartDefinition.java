package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;

public class PartDefinition extends MapItemDefinition<Part> {

	public PartDefinition(String URI, Player owner, GameState gameState)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner, gameState);
	}

	@Override
	public Part createMapItem(Transformation t) {
		return new Part(t, this);
	}

	@Override
	protected void forceSubclassReloadConfigData() {
				
	}

}
