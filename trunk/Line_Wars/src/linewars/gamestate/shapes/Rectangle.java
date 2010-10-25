package linewars.gamestate.shapes;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.parser.Parser;
import linewars.parser.Parser.NoSuchKeyException;
import linewars.parser.ParserKeys;

public class Rectangle extends Shape {
	
	static {
		Shape.addClassForInitialization("rectangle", Rectangle.class);
	}
	
	//TODO document
	private double width, height;
	private Transformation position;
	
	public Rectangle(Parser config){
		width = config.getNumericValue(ParserKeys.width);
		height = config.getNumericValue(ParserKeys.height);
		double rotation = 0;
		try{
			rotation = Math.PI * config.getNumericValue(ParserKeys.rotation);
		}catch(NoSuchKeyException e){
			//Just means rotation wasn't set, so it defaults to 0
		}
		position = new Transformation(new Position(config.getNumericValue(ParserKeys.x), config.getNumericValue(ParserKeys.y)), rotation);
	}
	
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
	
	public Position[] getVertexPositions(){
		Position[] ret = new Position[4];
		Position halfWidth = new Position(Math.cos(position.getRotation()) * width, Math.sin(position.getRotation()) * width).scale(.5);
		Position halfHeight = new Position(Math.cos(position.getRotation()) * height, Math.sin(position.getRotation()) * height).scale(.5);
		ret[0] = position.getPosition().add(halfHeight).add(halfWidth);
		ret[1] = position.getPosition().add(halfHeight).subtract(halfWidth);
		ret[2] = position.getPosition().subtract(halfHeight).add(halfWidth);
		ret[3] = position.getPosition().subtract(halfHeight).subtract(halfWidth);
		return ret;
	}
}
