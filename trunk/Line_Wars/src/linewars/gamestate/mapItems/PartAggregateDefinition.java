package linewars.gamestate.mapItems;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;

public class PartAggregateDefinition extends MapItemAggregateDefinition<PartAggregate> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1251782006734065935L;

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
	
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof PartAggregateDefinition) &&
				super.equals(obj);
	}

}
