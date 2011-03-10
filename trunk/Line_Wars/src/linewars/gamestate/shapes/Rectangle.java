package linewars.gamestate.shapes;

import java.io.Serializable;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

/**
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp class Rectangle extends Shape {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5089963326851525171L;
	//the width and height of the Rectangle
	private double width, height;
	//the center and rotation of the Rectangle
	private Transformation position;
	
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
	public Rectangle transform(Transformation change) {
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
	
	/**
	 * 
	 * @return The positions of the four vertices of the Rectangle, in counterclockwise order.
	 */
	public Position[] getVertexPositions(){
		Position[] ret = new Position[4];
		Position halfWidth = new Position(Math.cos(position.getRotation()) * width, Math.sin(position.getRotation()) * width).scale(.5);
		Position halfHeight = new Position(-Math.sin(position.getRotation()) * height, Math.cos(position.getRotation()) * height).scale(.5);
		ret[0] = position.getPosition().add(halfHeight).add(halfWidth);
		ret[1] = position.getPosition().add(halfHeight).subtract(halfWidth);
		ret[2] = position.getPosition().subtract(halfHeight).subtract(halfWidth);
		ret[3] = position.getPosition().subtract(halfHeight).add(halfWidth);
		return ret;
	}
	
	/**
	 * 
	 * @return Vectors describing the length and direction of the four edges of the Rectangle, in counterclockwise order
	 */
	public Position[] getEdgeVectors(){
		Position[] ret = new Position[4];
		Position w = new Position(Math.cos(position.getRotation()) * width, Math.sin(position.getRotation()) * width);
		Position h = new Position(Math.cos(position.getRotation()) * height, Math.sin(position.getRotation()) * height);
		ret[0] = w;
		ret[1] = h;
		ret[2] = w.scale(-1);
		ret[3] = h.scale(-1);
		return ret;
	}

	@Override
	public boolean positionIsInShape(Position toTest) {
		//TODO test
		//ray-casting algorithm, look it up.  Implementation might be wrong :(
		int numCrossings = 0;
		Position[] vertices = getVertexPositions();
		
		for(int i = 0, j = vertices.length - 1; i < vertices.length; j = i, i++){
			if(pointRayCrossesSegment(toTest, vertices[i], vertices[j])){
					numCrossings++;
				}
		}
		
		return numCrossings % 2 == 1;
	}
	
	private boolean pointRayCrossesSegment(Position p, Position a, Position b)
	{
		double px = p.getX();
		double py = p.getY();
		double ax = a.getX();
		double ay = a.getY();
		double bx = b.getX();
		double by = b.getY();
		
		//if p is above or below the segment
		if(py > Math.max(ay, by) || py < Math.min(ay, by))
			return false;
		//if p is to the right of the segment
		if(px > Math.max(ax, bx))
			return false;
		//if p is to the left of the segment
		if(px < Math.min(ax, bx))
			return true;
		
		//find the angle from a to b and from a to p
		double anglePA = Math.abs(Math.atan2(py - ay, px - ax));
		double angleBA = Math.abs(Math.atan2(by - ay, bx - ax));
		
		//if the angle from a to p is greater than the angle from a to b
		if(anglePA >= angleBA)
			return true;
		else
			return false;
	}
	
	//Very strict; these Rectangles will only be considered equal if they are bit-identical.
	@Override
	public boolean equals(Object other){
		if(other == null) return false;
		if(!(other instanceof Rectangle)) return false;
		Rectangle otherRect = (Rectangle) other;
		if(width != otherRect.width) return false;
		if(height != otherRect.height) return false;
		if(!position.equals(otherRect.position)) return false;
		return true;
	}
}
