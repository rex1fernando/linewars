package linewars.gamestate.mapItems;

import linewars.gamestate.Node;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;

/**
 * 
 * @author , Connor Schenck
 *
 * This class represents a building. It is a type of map item.
 * It knows what nodes it is in.
 */
public strictfp class Building extends MapItem {
	
	private BuildingDefinition definition;
	private Node node;

	/**
	 * Constructs a building at t with def as its creating definition
	 * and n as the node it is in.
	 * 
	 * @param t		the transformation to place this building at
	 * @param def	the map item definition that created this building
	 * @param n		the node that contains this building
	 */
	public Building(Transformation t, BuildingDefinition def, Node n) {
		super(t, def);
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
