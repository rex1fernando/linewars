package linewars.gamestate.shapes;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

/**
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp class Circle extends Shape {
	
	
	//the position of the Circle's center
	private final Transformation position;
	
	//the Circle's radius
	private final double radius;
	
	/**
	 * Constructs a Circle with the supplied center and radius.
	 * @param pos
	 * The center of the Circle
	 * @param radius
	 * The radius of the Circle
	 */
	public Circle(Transformation pos, double radius){
		this.radius = radius;
		position = pos;
	}

	@Override
	public Shape stretch(Transformation change) {
		double length = radius * 2 + Math.sqrt(change.getPosition().distanceSquared(new Position(0, 0)));
		double width = radius * 2;
		double rotation = Math.atan(change.getPosition().getY() / change.getPosition().getX());
		return new Rectangle(new Transformation(position.getPosition().add(change.getPosition().scale(.5)), rotation), length, width);
	}

	@Override
	public Circle transform(Transformation change) {
		return new Circle(new Transformation(position.getPosition().add(change.getPosition()), position.getRotation() + change.getRotation()), radius);
	}

	@Override
	public Transformation position() {
		return position;
	}

	@Override
	public Circle boundingCircle() {
		return this;
	}

	@Override
	public Rectangle boundingRectangle() {
		return new Rectangle(position, radius * 2, radius * 2);
	}
	
	/**
	 * Returns the radius of the circle.
	 */
	public double getRadius(){
		return radius;
	}

	@Override
	public boolean positionIsInShape(Position toTest) {
		return toTest.distanceSquared(position.getPosition()) < radius * radius;
	}
	
	@Override
	public String toString(){
		return "Center = " + position + " Radius = " + radius;
	}
	
	//Yes, I'm checking doubles for equality.  That's on purpose.
	@Override
	public boolean equals(Object other){
		if(other == null) return false;
		if(!(other instanceof Circle)) return false;
		Circle otherCircle = (Circle) other;
		if(!otherCircle.position.equals(position)) return false;
		if(!(otherCircle.radius == radius)) return false;
		return true;
	}
	
	@Override
	public AABB calculateAABB()
	{
		Position p = position.getPosition();
		double x = p.getX();
		double y = p.getY();
		
		return new AABB(x-radius, y-radius, x+radius, y+radius);
	}
}
