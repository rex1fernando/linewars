package linewars.gamestate.mapItems;


import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;

public class PartDefinition extends MapItemDefinition<Part> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6955722405607312188L;

	public PartDefinition() {
		super();
	}

	@Override
	public Part createMapItem(Transformation t, Player owner, GameState gameState) {
		return new Part(t, this, owner, gameState);
	}

	@Override
	protected void forceSubclassReloadConfiguration() {
				
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof PartDefinition) &&
				super.equals(obj);
	}

}
