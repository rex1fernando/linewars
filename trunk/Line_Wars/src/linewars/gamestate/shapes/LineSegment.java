package linewars.gamestate.shapes;

import java.util.ArrayList;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

public class LineSegment extends Shape {
	/**
	 * 
	 */
	private static final long serialVersionUID = 937619229537379883L;
	Position start, end;
	
	public LineSegment(Position p1, Position p2){
		start = p1;
		end = p2;
	}
	
	public Position start(){
		return start;
	}
	
	public Position end(){
		return end;
	}
	
	public boolean pointIsInLeftHalfspace(Position query){
		Position segmentVector = end.subtract(start);
		Position queryVector = query.subtract(start);
		double rotatedX = segmentVector.getY();
		double rotatedY = segmentVector.getX() * -1;
		Position rotatedSegmentVector = new Position(rotatedX, rotatedY);
		return rotatedSegmentVector.dot(queryVector) <= 0;
	}

	@Override
	public Shape stretch(Transformation change) {
		LineSegment target = transform(change);
		Position allVertices = start.add(end).add(target.start).add(target.end);
		Triangle subTriangleS = new Triangle(new Transformation(allVertices.subtract(start).scale(1 / 3.0), 0), target.start, end);
		Triangle subTriangleE = new Triangle(new Transformation(allVertices.subtract(end).scale(1 / 3.0), 0), target.start, start);
		Triangle subTriangleTS = new Triangle(new Transformation(allVertices.subtract(target.start).scale(1 / 3.0), 0), start, end);
		Triangle subTriangleTE = new Triangle(new Transformation(allVertices.subtract(target.end).scale(1 / 3.0), 0), start, end);
		
		ArrayList<Shape> triangles = new ArrayList<Shape>();
		triangles.add(subTriangleS);
		triangles.add(subTriangleE);
		triangles.add(subTriangleTS);
		triangles.add(subTriangleTE);
		
		ArrayList<Transformation> relativeTransformations = new ArrayList<Transformation>();
		Transformation pos = position();
		for(Shape currentShape : triangles){
			Position relativePosition = currentShape.position().getPosition().subtract(pos.getPosition());
			double relativeRotation = currentShape.position().getRotation() - pos.getRotation();
			relativeTransformations.add(new Transformation(relativePosition, relativeRotation));
		}

		return new ShapeAggregate(pos, triangles, relativeTransformations);
	}

	@Override
	public LineSegment transform(Transformation change) {
		Position rotatedStart = start.rotateAboutPosition(position().getPosition(), change.getRotation());
		Position rotatedEnd = end.rotateAboutPosition(position().getPosition(), change.getRotation());
		Position finalStart = rotatedStart.add(change.getPosition());
		Position finalEnd = rotatedEnd.add(change.getPosition());
		return new LineSegment(finalStart, finalEnd);
	}

	@Override
	public Transformation position() {
		return new Transformation(start.add(end).scale(0.5), end.subtract(start).getAngle());
	}

	@Override
	public Circle boundingCircle() {
		return new Circle(position(), end.subtract(start).length() / 2);
	}

	@Override
	public Rectangle boundingRectangle() {
		return new Rectangle(position(), end.subtract(start).length(), 0);
	}

	@Override
	public boolean positionIsInShape(Position toTest) {
		return false;
	}

	@Override
	public AABB calculateAABB() {
		double xMin = end.getX();
		double xMax = start.getX();
		if(start.getX() < end.getX()){
			xMin = start.getX();
			xMax = end.getX();
		}
		
		double yMin = end.getY();
		double yMax = start.getY();
		if(start.getY() < end.getY()){
			yMin = start.getY();
			yMax = end.getY();
		}
		return new AABB(xMin, xMax, yMin, yMax);
	}

	@Override
	public Shape scale(double scaleFactor) {
		Position center = position().getPosition();
		Position newStart = start.subtract(center).scale(scaleFactor).add(center);
		Position newEnd = end.subtract(center).scale(scaleFactor).add(center);
		return new LineSegment(newStart, newEnd);
	}
	
	@Override
	public boolean equals(Object other){
		if(other == null){
			return false;
		}
		if(!(other instanceof LineSegment)){
			return false;
		}
		LineSegment ls = (LineSegment) other;
		if(ls.start().equals(start()) && ls.end().equals(end())){
			return true;
		}else if(ls.start().equals(end()) && ls.end().equals(start())){
			return true;
		}
		return false;
	}
}
