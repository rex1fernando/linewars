package linewars.gamestate.mapItems;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;

public class LaneBorder extends MapItem {
	
	private LaneBorderDefinition lbd = null;

	public LaneBorder(Transformation trans, LaneBorderDefinition ld) {
		super(trans, null);
		lbd = ld;
	}

	@Override
	public CollisionStrategy getCollisionStrategy() {
		return lbd.cStrat;
	}

	@Override
	protected MapItemDefinition getDefinition() {
		return lbd;
	}

}
