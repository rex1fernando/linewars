package linewars.gamestate.mapItems;


import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;

public class PartDefinition extends MapItemDefinition<Part> {

	public PartDefinition() {
		super();
	}

	@Override
	public Part createMapItem(Transformation t, Player owner, GameState gameState) {
		return new Part(t, this, owner, gameState);
	}

	@Override
	protected void forceSubclassReloadConfigData() {
				
	}

}
