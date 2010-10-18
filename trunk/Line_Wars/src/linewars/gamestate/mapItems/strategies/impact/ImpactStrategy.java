package linewars.gamestate.mapItems.strategies.impact;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;

public interface ImpactStrategy {
	
	public ImpactStrategy createInstanceOf(MapItem m);
	
	public void handleImpact(MapItem m);
	
	public void handleImpact(Position p);

}
