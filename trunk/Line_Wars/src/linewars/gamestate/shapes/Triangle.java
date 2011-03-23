package linewars.gamestate.shapes;

import java.util.ArrayList;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

public strictfp class Triangle extends Shape {

	private final Position[] corners;
	private final Transformation center;
	
	public Triangle(Transformation center, Position cornerOne, Position cornerTwo){
		Position partialAverage = cornerOne.add(cornerTwo).scale(.5);
		Position offset = center.getPosition().subtract(partialAverage).scale(2);
		Position cornerThree = center.getPosition().add(offset);
		
		corners = new Position[3];
		corners[0] = cornerOne;
		corners[1] = cornerTwo;
		corners[2] = cornerThree;
		this.center = center;
	}
	
	@Override
	public Shape stretch(Transformation change) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Shape transform(Transformation change) {
		Position[] corners = new Position[2];
		Transformation center;
		//for each point
		for(int i = 0; i < 2; i++){
			//rotate it about the current center
			corners[i] = this.corners[i].rotateAboutPosition(this.center.getPosition(), change.getRotation());
			//then translate it
			corners[i] = this.corners[i].add(change.getPosition());
		}
		//move the center by change
		center = this.center.add(change);
		
		return new Triangle(center, corners[0], corners[1]);
	}

	@Override
	public Transformation position() {
		return center;
	}

	@Override
	public Circle boundingCircle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle boundingRectangle() {
		//figure out which edge is longest, it will be one of the two long edges of the rectangle
		Position longestEdge = new Position(0, 0);
		int excludedCorner = 0;
		ArrayList<Position> longestEdgeCorners = null;
		for(int i = 0; i < 3; i++){
			ArrayList<Position> includedCorners = new ArrayList<Position>();
			for(int j = 0; j < 3; j++){
				if(i != j){
					includedCorners.add(corners[j]);
				}
			}
			Position currentEdge = includedCorners.get(1).subtract(includedCorners.get(0));
			if(currentEdge.length() > longestEdge.length()){
				longestEdge = currentEdge;
				excludedCorner = i;
				longestEdgeCorners = includedCorners;
			}
		}
		//that is the width of the rectangle
		//compute height
		//projection onto ortho of long side of the left-out corner minus one of the endpoints of the long side
		Position heightVector = corners[excludedCorner].subtract(longestEdgeCorners.get(0)).vectorProjection(longestEdge.orthogonal());
		//compute center
		longestEdgeCorners.add(longestEdgeCorners.get(0).add(heightVector));
		longestEdgeCorners.add(longestEdgeCorners.get(1).add(heightVector));
		Position center = new Position(0, 0);
		for(int i = 0; i < 4; i++){
			center.add(longestEdgeCorners.get(i));
		}
			//longestedge / 2 + height / 2
		//compute rotation
			//trig with the longest edge vector
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean positionIsInShape(Position toTest) {
		// TODO Auto-generated method stub
		return false;
	}

}
