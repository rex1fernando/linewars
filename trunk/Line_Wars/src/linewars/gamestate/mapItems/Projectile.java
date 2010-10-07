package linewars.gamestate.mapItems;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.strategies.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.ImpactStrategy;

public class Projectile extends MapItem {

	private ProjectileDefinition definition;
	private CollisionStrategy cStrat;
	private ImpactStrategy iStrat;
	
	public Projectile(Position p, double rot, ProjectileDefinition def, CollisionStrategy cs, ImpactStrategy is) {
		super(p, rot);
		definition = def;
		cStrat = cs.createInstanceOf(this);
		iStrat = is.createInstanceOf(this);
	}
	
	//TODO NOTE: this will be changed to implement a projectileMovementStrategy later
	public void move()
	{
		double v = definition.getVelocity();
		double r = this.rotation;
		Position change = this.pos.add(v*Math.sin(r), v*Math.cos(r));
		
		//TODO some method for checking for collisions from the current
		//position to the new position
		
		this.setPosition(this.getPosition().add(change));
		
		//TODO this array is assumed returned by the above todo in
		//order of collision (first collision is first)
		Unit[] collisions = null;
		//there's no need to call the collision strategy, it was taken into account when calculating collision
		for(int i = 0; i < collisions.length && !this.getState().equals(MapItemState.Dead); i++)
			iStrat.handleImpact(collisions[i]);
	}

	@Override
	protected MapItemDefinition getDefinition() {
		return definition;
	}

	@Override
	public CollisionStrategy getCollisionStrategy() {
		return cStrat;
	}
	
	public ImpactStrategy getImpactStrategy()
	{
		return iStrat;
	}

}
