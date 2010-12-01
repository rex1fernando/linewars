package linewars.gamestate.mapItems;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;

/**
 * 
 * @author Connor Schenck
 *
 */
public strictfp class LaneBorder extends MapItem {
	
	private LaneBorderDefinition lbd = null;

	public LaneBorder(Transformation trans, LaneBorderDefinition ld) {
		super(trans, ld);
		lbd = ld;
	}

	@Override
	public CollisionStrategy getCollisionStrategy() {
		return lbd.cStrat;
	}

	@Override
	public MapItemDefinition getDefinition() {
		return lbd;
	}

}
