package linewars.gamestate.shapes;

import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

/**
 * Represents a collection of Shapes, each with a specific relative position and orientation.
 * 
 * 
 * @author Taylor Bergquist
 *
 */
public strictfp class ShapeAggregate extends Shape {
	
	private Position center;
	private ArrayList<Shape> members;
	private double rotation;
	
	private Rectangle boundingRectangle = null;
	private Circle boundingCircle = null;
	
	public ShapeAggregate(Transformation center, ArrayList<Shape> shapes, ArrayList<Transformation> relativePositions){
		this();
		for(int i = 0; i < shapes.size(); i++){
			Shape currentShape = shapes.get(i);
			Transformation current = currentShape.position();
			Transformation target = relativePositions.get(i);
			
			//final - initial
			Transformation change = new Transformation(target.getPosition().subtract(current.getPosition()), target.getRotation() - current.getRotation());
			members.add(currentShape.transform(change));
		}
		this.transform(center);
	}

	private ShapeAggregate() {
		center = new Position(0, 0);
		members = new ArrayList<Shape>();
		rotation = 0;
	}

	
	@Override
	public Shape stretch(Transformation change) {
		//compute destination Shape
		ShapeAggregate destination = (ShapeAggregate) transform(change);
		
		ShapeAggregate ret = new ShapeAggregate();
		
		ret.center = center.add(change.getPosition().scale(.5));
		
		//for each sub-Shape
		for(int i = 0; i < members.size(); i++){
			//get the ones we are dealing with
			Shape source = members.get(i);
			Shape target = destination.members.get(i);
			
			//compute the Transformation that transforms one to the other
			Position deltaP = target.position().getPosition().subtract(source.position().getPosition());
			double deltaR = target.position().getRotation() - source.position().getRotation();
			
			//stretch from this to target by the computed transformation
			Shape stretchedSubShape = source.stretch(new Transformation(deltaP, deltaR));
			
			//add it to ret
			ret.members.add(stretchedSubShape);
		}
		
		//set ret's rotation - though this is really kind of meaningless in this case.
		ret.rotation = destination.rotation;
		return ret;
	}

	@Override
	public ShapeAggregate transform(Transformation change) {
		ShapeAggregate ret = new ShapeAggregate();
		
		ret.center = center.add(change.getPosition());
		
		//for each Shape
		for(int i = 0; i < members.size(); i++){
			//translate by -1 * this.position
			Shape atOrigin = members.get(i).transform(new Transformation(position().getPosition().scale(-1), 0));
			//rotate by change
			Shape rotated = atOrigin.transform(new Transformation(new Position(0, 0), change.getRotation()));
			//translate by this.position and change
			Shape finalShape = rotated.transform(new Transformation(change.getPosition().add(position().getPosition()), 0));
			ret.members.add(finalShape);
		}
		ret.rotation = rotation + change.getRotation();
		
		//performance optimizations
		if(boundingRectangle != null){
			ret.boundingRectangle = boundingRectangle.transform(change);
		}
		if(boundingCircle != null){
			ret.boundingCircle = boundingCircle.transform(change);
		}
		return ret;
	}

	@Override
	public Transformation position() {
		return new Transformation(center, rotation);
	}
	
	/**
	 * Returns the Shapes which compose this ShapeAggregate
	 */
	public Shape[] getMembers() {
		return members.toArray(new Shape[0]);
	}

	@Override
	public Circle boundingCircle() {
		if(boundingCircle != null){
			return boundingCircle;
		}
		
		return boundingRectangle().boundingCircle();
	}

	@Override
	public Rectangle boundingRectangle() {
		if(boundingRectangle != null){
			return boundingRectangle;
		}
		
		//unrotate this to ensure that the computed bounding rect is oriented with the ShapeAggregate
		ShapeAggregate unrotated = this.transform(new Transformation(new Position(0, 0), rotation));
		
		//compute the bounds
		double minX = Double.MAX_VALUE;
		double minY = minX;
		double maxX = minX * -1;
		double maxY = maxX;
		
		for(Shape currentShape : unrotated.members){
			Rectangle subBoundingRect = currentShape.boundingRectangle();
			Position[] subPositions = subBoundingRect.getVertexPositions();
			for(Position currentPosition : subPositions){
				if(currentPosition.getX() < minX){
					minX = currentPosition.getX();
				}
				if(currentPosition.getX() > maxX){
					maxX = currentPosition.getX();
				}
				if(currentPosition.getY() < minY){
					minY = currentPosition.getY();
				}
				if(currentPosition.getY() > maxY){
					maxY = currentPosition.getY();
				}
			}
		}

		//construct the rectangle from the computed bounds
		double width = maxX - minX;
		double height = maxY - minY;
		Position boundingRectangleCenter = new Position(minX + width / 2, minY + height / 2);
		
		Rectangle ret = new Rectangle(new Transformation(boundingRectangleCenter, rotation), width, height);
		return ret;
	}

	@Override
	public boolean positionIsInShape(Position toTest) {
		//The point is in the Shape if it is in any of the sub-shapes
		for(Shape currentShape : members){
			if(currentShape.positionIsInShape(toTest)){
				return true;
			}
		}
		return false;
	}
	
	public AABB calculateAABB()
	{
		return boundingRectangle().calculateAABB();
	}
}
