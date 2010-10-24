package linewars.gamestate.shapes;

import linewars.gamestate.Transformation;

public class Rectangle extends Shape {
	//TODO document
	private double width, height;
	private Transformation position;
	
	//TODO document
	public Rectangle(Transformation position, double width, double height){
		this.position = position;
		this.width = width;
		this.height = height;
	}

	@Override
	public Shape stretch(Transformation change) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Shape transform(Transformation change) {
		Transformation newTransform = new Transformation(position.getPosition().add(change.getPosition()), position.getRotation() + change.getRotation());
		return new Rectangle(newTransform, width, height);
	}

	@Override
	public Transformation position() {
		return position;
	}

	@Override
	public Circle boundingCircle() {
		// compute diagonal
		double diagonal = Math.sqrt(width * width + height * height);
		// construct and return a Circle
		return new Circle(position, diagonal / 2);
	}

	@Override
	public Rectangle boundingRectangle() {
		return this;
	}
	
	/**
	 * 
	 * @return The width of the rectangle, ignoring any rotation.
	 */
	public double getWidth(){
		return width;
	}
	
	/**
	 * 
	 * @return The height of the rectangle, ignoring any rotation.
	 */
	public double getHeight(){
		return height;
	}
}
