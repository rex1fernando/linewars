package linewars.gamestate.shapes;

import linewars.gamestate.Transformation;

public class Circle extends Shape {
	
	//TODO document
	private Transformation position;
	private double radius;
	
	//TODO document
	public Circle(Transformation pos, double radius){
		this.radius = radius;
		position = pos;
	}

	@Override
	public Shape stretch(Transformation change) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Shape transform(Transformation change) {
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
}
