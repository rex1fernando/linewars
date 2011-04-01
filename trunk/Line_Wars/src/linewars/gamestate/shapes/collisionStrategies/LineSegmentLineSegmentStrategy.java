package linewars.gamestate.shapes.collisionStrategies;

import linewars.gamestate.Position;
import linewars.gamestate.shapes.LineSegment;
import linewars.gamestate.shapes.Shape;

public class LineSegmentLineSegmentStrategy extends ShapeCollisionStrategy {

	@Override
	public boolean collides(Shape first, Shape second) {
		if(first.getClass() != LineSegment.class || second.getClass() != LineSegment.class){
			throw new UnsupportedOperationException(getClass() + " does not support collision detection between " + first.getClass() + " and " + second.getClass() + ".");
		}
		
		LineSegment f = (LineSegment) first;
		LineSegment s = (LineSegment) second;
		
		Position intersectionPoint = lineLineIntersectionPoint(f, s);
		
		if(intersectionPoint == null){//if the lines are parallel
			return f.equals(s);//the lines could still be the same line, in which case they do collide
		}
		
		return pointOnLineIsOnSegment(f, intersectionPoint) && pointOnLineIsOnSegment(s, intersectionPoint);
	}
	
	private Position lineLineIntersectionPoint(LineSegment first, LineSegment second){
		double[] x = {first.start().getX(), first.end().getX(), second.start().getX(), second.end().getX()};
		double[] y = {first.start().getY(), first.end().getY(), second.start().getY(), second.end().getY()};
		double denom = (y[3] - y[2]) * (x[1] - x[0]) - (x[3] - x[2]) * (y[1] - y[0]);
		
		if(Math.abs(denom) - 0 < 1e-10){//if the lines are very nearly parallel
			return null;
		}
		
		double numer = (x[3] - x[2]) * (y[0] - y[2]) - (y[3] - y[2]) * (x[0] - x[2]);
		
		double a = numer / denom;
		double newX = x[0] + a * (x[1] - x[0]);
		double newY = y[0] + a * (y[1] - y[0]);
		return new Position(newX, newY);
	}
	
	private boolean pointOnLineIsOnSegment(LineSegment segment, Position query){
		double length = segment.end().subtract(segment.start()).length();
		return length >= segment.end().subtract(query).length() && length >= segment.start().subtract(query).length();
	}

}