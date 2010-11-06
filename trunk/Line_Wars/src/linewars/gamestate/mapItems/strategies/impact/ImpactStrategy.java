package linewars.gamestate.mapItems.strategies.impact;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;

/**
 * 
 * @author cschenck
 *
 * This interface specifies how a projectile handles impacting
 * mapItems.
 */
public strictfp interface ImpactStrategy {
	
	/**
	 * Creates a new instance of the same type of ImpactStrategy
	 * with the given mapItem as the owner.
	 * 
	 * @param m		the mapItem to own the new strategy
	 * @return		the new strategy
	 */
	public ImpactStrategy createInstanceOf(MapItem m);
	
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
