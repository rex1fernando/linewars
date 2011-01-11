package linewars.gamestate.mapItems;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;

public class Part extends MapItem {

	private PartDefinition def;
	
	public Part(Transformation trans, PartDefinition def, Player owner, GameState gameState) {
		super(trans, def, owner, gameState);
		this.def = def;
	}

	@Override
	public MapItemDefinition<? extends MapItem> getDefinition() {
		return def;
	}

}
