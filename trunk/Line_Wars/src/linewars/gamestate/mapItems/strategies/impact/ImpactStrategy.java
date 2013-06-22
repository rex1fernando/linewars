package linewars.gamestate.mapItems.strategies.impact;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.strategies.Strategy;

/**
 * 
 * @author , Connor Schenck
 *
 * This interface specifies how a projectile handles impacting
 * mapItems.
 */
public strictfp interface ImpactStrategy extends Strategy<ImpactStrategyConfiguration> {
	
	/**
	 * Tells this impact strategy to handle an impact with a
	 * mapItem.
	 * 
	 * @param m		the mapItem that was impacted.
	 */
	public void handleImpact(MapItem m);
	
	/**
	 * Tells this impact strategy to handle an impact with the
	 * ground at the give position.
	 * 
	 * @param p		the position that the ground was impacted at.
	 */
	public void handleImpact(Position p);

}
