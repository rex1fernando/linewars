package linewars.gamestate.mapItems;

import java.util.ArrayList;

import linewars.gamestate.Lane;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.impact.ImpactStrategy;
import linewars.gamestate.shapes.ShapeAggregate;

/**
 * 
 * @author cschenck
 *
 * This class represents a projectile. It knows how the projectile collides
 * with map items and what it should do upon impact. It is a map item.
 */
public class Projectile extends MapItem {

	private ProjectileDefinition definition;
	private CollisionStrategy cStrat;
	private ImpactStrategy iStrat;
	private Lane lane;
	
	private ShapeAggregate tempBody = null;
	
	public Projectile(Transformation t, ProjectileDefinition def, CollisionStrategy cs, ImpactStrategy is, Lane l) {
		super(t);
		definition = def;
		cStrat = cs.createInstanceOf(this);
		iStrat = is.createInstanceOf(this);
		lane = l;
	}
	
	//TODO NOTE: this will be changed to implement a projectileMovementStrategy later
	/**
	 * This method moves the projetile forward at a constant velocity, checks for collisions,
	 * and calls its impact strategy on those collisions.
	 */
	public void move()
	{
		double v = definition.getVelocity();
		double r = this.getRotation();
		Position change = this.getPosition().add(v*Math.sin(r), v*Math.cos(r));
		
		//this is the raw list of items colliding with this projetile's path
		MapItem[] rawCollisions = lane.getCollisions(this);
		//this list will be the list of how far along that path each map item is
		double[] scores = new double[rawCollisions.length];
		//the negative sine of the angle that the path was rotated by from 0 rads
		double sine = -change.getY();
		//the negative cosine of the angle that the path was rotated by from 0 rads
		double cosine = -change.getX();
		//calculate the x coordinate of each map item relative to this projectile and rotated
		//"back" from how this projetile's path was rotated. This will define the order in which
		//the projectile hit each map item
		for(int i = 0; i < scores.length; i++)
		{
			Position p = rawCollisions[i].getPosition().subtract(this.getPosition());
			scores[i] = cosine*p.getX() - sine*p.getY();
		}
			
		MapItem[] collisions = new MapItem[scores.length];
		
		//since this list will never be that big, its ok to use selection sort
		for(int i = 0; i < collisions.length; i++)
		{
			int smallest = 0;
			for(int j = 1; j < collisions.length; j++)
				if(scores[j] < scores[smallest])
					smallest = j;
			
			collisions[i] = rawCollisions[smallest];
			scores[smallest] = Double.MAX_VALUE;
		}
		
		//move the projectile before calling the impact strategy so that the impact strategy may move the projectile
		this.setPosition(this.getPosition().add(change));
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
	
	/**
	 * 
	 * @return	the impact strategy of this projetile
	 */
	public ImpactStrategy getImpactStrategy()
	{
		return iStrat;
	}
	
	@Override
	public boolean isCollidingWith(MapItem m)
	{
		if(tempBody == null)
			return super.isCollidingWith(m);
		else
		{
			if(!m.getCollisionStrategy().canCollideWith(this))
				return false;
			else
				return tempBody.isCollidingWith(getTransformation(), m.getDefinition().getBody(), m.getTransformation());
		}
	}

}
