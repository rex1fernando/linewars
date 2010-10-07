package linewars.gamestate.mapItems;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.strategies.CollisionStrategy;

public class Building extends MapItem {
	
	private BuildingDefinition definition;
	private Node node;

	public Building(Position p, double rot, BuildingDefinition def, Node n) {
		super(p, rot);
		definition = def;
		node = n;
	}

	@Override
	protected MapItemDefinition getDefinition() {
		return definition;
	}
	
	public Node getNode()
	{
		return node;
	}

	@Override
	public CollisionStrategy getCollisionStrategy() {
		return definition.getCollisionStrategy();
	}

}
