package linewars.gamestate.mapItems;

import linewars.gamestate.Position;

public class Projectile extends MapItem {

	private ProjectileDefinition definition;
	
	public Projectile(Position p, double rot, ProjectileDefinition def) {
		super(p, rot);
		definition = def;
	}
	
	public Unit[] move()
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
		return collisions;
		
	}

	@Override
	protected MapItemDefinition getDefinition() {
		return definition;
	}

}
