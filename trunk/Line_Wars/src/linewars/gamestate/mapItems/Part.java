package linewars.gamestate.mapItems;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;

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

	@Override
	protected void setDefinition(MapItemDefinition<? extends MapItem> def) {
		this.def = (PartDefinition) def;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof Part) &&
				super.equals(obj);
	}

}
