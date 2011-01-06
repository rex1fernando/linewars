package linewars.gamestate.mapItems;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;

public class PartAggregate extends MapItemAggregate {

	private PartAggregateDefinition def;
	private CollisionStrategy colStrat;
	
	public PartAggregate(Transformation trans, PartAggregateDefinition def) {
		super(trans, def);
		this.def = def;
		colStrat = def.getCollisionStrategy().createInstanceOf(this);
	}

	@Override
	public MapItemDefinition<? extends MapItem> getDefinition() {
		return def;
	}

	@Override
	public CollisionStrategy getCollisionStrategy() {
		return colStrat;
	}

}
