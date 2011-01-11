package linewars.gamestate.mapItems;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;

public class PartAggregateDefinition extends MapItemAggregateDefinition<PartAggregate> {

	public PartAggregateDefinition() {
		super();
	}

	@Override
	protected PartAggregate createMapItemAggregate(Transformation t, Player owner, GameState gameState) {
		return new PartAggregate(t, this, owner, gameState);
	}

	@Override
	protected void forceAggregateSubReloadConfigData() {
		
	}

}
