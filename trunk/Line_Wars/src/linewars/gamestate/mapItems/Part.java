package linewars.gamestate.mapItems;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;

public class Part extends MapItem {

	private PartDefinition def;
	private CollisionStrategy colStrat;
	
	public Part(Transformation trans, PartDefinition def) {
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
