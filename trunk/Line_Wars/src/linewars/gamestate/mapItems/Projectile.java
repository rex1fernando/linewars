package linewars.gamestate.mapItems;


import linewars.gamestate.Lane;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.impact.ImpactStrategy;
import linewars.gamestate.shapes.Shape;

/**
 * 
 * @author , Connor Schenck
 *
 * This class represents a projectile. It knows how the projectile collides
 * with map items and what it should do upon impact. It is a map item.
 */
public strictfp class Projectile extends MapItem {

	private ProjectileDefinition definition;
	private CollisionStrategy cStrat;
	private ImpactStrategy iStrat;
	private Lane lane;
	
	private Shape tempBody = null;
	
	/**
	 * Creates a projectile at transformation t with definition def,
	 * collision strategy cs, and impact strategy is. Sets its container
	 * to l.
	 * 
	 * @param t		the transformation this projectile starts at
	 * @param def	the definition that created this projectile
	 * @param cs	the collision strategy for this projectile
	 * @param is	the impact strategy for this projectile
	 * @param l		the lane that this projectile is in
	 */
	public Projectile(Transformation t, ProjectileDefinition def, CollisionStrategy cs, ImpactStrategy is, Lane l) {
		super(t, def);
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
		//first check to see if this unit is outside the lane
		Transformation t = lane.getPosition(lane.getClosestPointRatio(this.getPosition()));
		if(this.getPosition().distanceSquared(t.getPosition()) > Math.pow(lane.getWidth()/2, 2))
		{
			this.setState(MapItemState.Dead);
			return;
		}
		
		double v = definition.getVelocity();
		double r = this.getRotation();
		Position change = new Position(v*Math.cos(r), v*Math.sin(r));
		
		tempBody = this.getBody().stretch(new Transformation(change, this.getRotation()));
		
		//this is the raw list of items colliding with this projetile's path
		MapItem[] rawCollisions = lane.getCollisions(this);
		tempBody = null;
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
		Position thisOne = this.getPosition();
		thisOne = thisOne.add(change);
		this.setPosition(thisOne);
		//there's no need to call the collision strategy, it was taken into account when calculating collision
		for(int i = 0; i < collisions.length && !this.getState().equals(MapItemState.Dead); i++)
			iStrat.handleImpact(collisions[i]);
	}

	@Override
	public MapItemDefinition getDefinition() {
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
				return tempBody.isCollidingWith(m.getBody());
		}
	}
	
	@Override
	public void setRotation(double rot)
	{
		super.setRotation(rot);
	}
	
	@Override
	public void setTransformation(Transformation t)
	{
		super.setTransformation(t);
	}

	@Override
	public boolean equals(Object o){
		if(o == null) return false;
		if(!(o instanceof Projectile)) return false;
		Projectile other = (Projectile) o;
		if(!other.getBody().equals(getBody())) return false;
		//TODO test other things in here
		return true;
	}
}
