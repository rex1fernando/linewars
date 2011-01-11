package linewars.gamestate.mapItems;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;

public class PartAggregate extends MapItemAggregate {

	private PartAggregateDefinition def;
	
	public PartAggregate(Transformation trans, PartAggregateDefinition def, Player owner, GameState gameState) {
		super(trans, def, gameState, owner);
		this.def = def;
	}

	@Override
	public MapItemDefinition<? extends MapItem> getDefinition() {
		return def;
	}

}
