package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;

public class PartAggregateDefinition extends MapItemAggregateDefinition<PartAggregate> {

	public PartAggregateDefinition(String URI, Player owner, GameState gameState)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner, gameState);
	}

	@Override
	protected PartAggregate createMapItemAggregate(Transformation t) {
		return new PartAggregate(t, this);
	}

	@Override
	protected void forceSubclassReloadConfigData() {
		
	}

}
