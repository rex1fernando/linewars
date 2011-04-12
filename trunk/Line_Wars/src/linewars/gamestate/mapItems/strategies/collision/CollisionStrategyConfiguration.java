package linewars.gamestate.mapItems.strategies.collision;

import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

public abstract class CollisionStrategyConfiguration extends StrategyConfiguration<CollisionStrategy> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7849319384722075322L;

	private static List<Class<? extends CollisionStrategyConfiguration>> orderedList = new ArrayList<Class<? extends CollisionStrategyConfiguration>>();
	
	static {
		StrategyConfiguration.setStrategyType("Collision", CollisionStrategyConfiguration.class);
		
		orderedList.add(GroundConfiguration.class);
		orderedList.add(GroundEnemiesOnlyConfiguration.class);
		orderedList.add(FlyingConfiguration.class);
		orderedList.add(AllEnemiesConfiguration.class);
		orderedList.add(AllEnemyUnitsConfiguration.class);
		orderedList.add(EnemyProjectilesConfiguration.class);
		orderedList.add(CollidesWithAllConfiguration.class);
		orderedList.add(NoCollisionConfiguration.class);
	}
	
	public static boolean isAllowedToCollide(MapItem c1, MapItem c2)
	{
		Class<? extends CollisionStrategyConfiguration> class1 = c1.getCollisionStrategy().getConfig().getClass();
		Class<? extends CollisionStrategyConfiguration> class2 = c2.getCollisionStrategy().getConfig().getClass();
		if(orderedList.indexOf(class1) > orderedList.indexOf(class2))
			return c1.getCollisionStrategy().canCollideWith(c2);
		else if(orderedList.indexOf(class1) < orderedList.indexOf(class2))
			return c2.getCollisionStrategy().canCollideWith(c1);
		else
			return c1.getCollisionStrategy().canCollideWith(c2) ||
			c2.getCollisionStrategy().canCollideWith(c1);
	}
}
