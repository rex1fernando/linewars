package linewars.gamestate.mapItems;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;

/**
 * 
 * @author cschenck
 *
 * This class represents a building. It is a type of map item.
 * It knows what nodes it is in.
 */
public class Building extends MapItem {
	
	private BuildingDefinition definition;
	private Node node;

	public Building(Transformation t, BuildingDefinition def, Node n) {
		super(t);
		definition = def;
		node = n;
	}

	@Override
	protected MapItemDefinition getDefinition() {
		return definition;
	}
	
	/**
	 * 
	 * @return	the node this building is in
	 */
	public Node getNode()
	{
		return node;
	}

	@Override
	public CollisionStrategy getCollisionStrategy() {
		return definition.getCollisionStrategy();
	}

}
