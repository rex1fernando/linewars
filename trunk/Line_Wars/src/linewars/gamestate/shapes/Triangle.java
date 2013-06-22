package linewars.gamestate.shapes;

import java.util.ArrayList;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

public strictfp class Triangle extends Shape {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8314252684618812816L;
	private final Position[] corners;
	private final Transformation center;
	
	private LineSegment[] edges = null;
	
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
		double dotABAB = corners[1].subtract(corners[0]).dot(corners[1].subtract(corners[0]));
	    double dotABAC = corners[1].subtract(corners[0]).dot(corners[2].subtract(corners[0]));
	    double dotACAC = corners[2].subtract(corners[0]).dot(corners[2].subtract(corners[0]));
	    double d = 2.0f*(dotABAB*dotACAC - dotABAC*dotABAC);
	    Position referencePt = corners[0];
	    //make sure the points aren't collinear
	    if(new Double(0).equals(Math.abs(d))){
	    	//they must be... let's compute the AABB and use the diagonal of that as a diameter for the circle
	    	AABB diagonal = calculateAABB();
	    	Position max = new Position(diagonal.getXMax(), diagonal.getYMax());
	    	Position min = new Position(diagonal.getXMin(), diagonal.getYMin());
	    	Position center = min.add(max).scale(0.5);
	    	Position relativeCorner = max.subtract(center);
	    	return new Circle(new Transformation(center, this.center.getRotation()), relativeCorner.length());
	    }
        double s = (dotABAB*dotACAC - dotACAC*dotABAC) / d;
        double t = (dotACAC*dotABAB - dotABAB*dotABAC) / d;
        Position center;
        if(s <= 0){//if ac is the longest side
        	center = corners[0].add(corners[2]).scale(0.5);
        }else if(t <= 0){
        	center = corners[0].add(corners[1]).scale(0.5);
        }else if(s + t >= 1){
        	center = corners[1].add(corners[2]).scale(0.5);
        	referencePt = corners[1];
        }else{
        	center = corners[0].add(corners[1].subtract(corners[0]).scale(s)).add(corners[2].subtract(corners[0]).scale(t));
        }
        double radius = Math.sqrt(corners[2].subtract(referencePt).dot(corners[2].subtract(referencePt)));
		
        return new Circle(new Transformation(center, this.center.getRotation()), radius);
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
			center = center.add(longestEdgeCorners.get(i));
		}
		center = center.scale(.25);
		
		double rotation = longestEdge.getAngle();
		
		return new Rectangle(new Transformation(center, rotation), longestEdge.length(), heightVector.length());
	}

	@Override
	public boolean positionIsInShape(Position toTest) {
		LineSegment[] edges = new LineSegment[3];
		edges[0] = new LineSegment(corners[0], corners[1]);
		edges[1] = new LineSegment(corners[1], corners[2]);
		edges[2] = new LineSegment(corners[2], corners[0]);
		
		boolean allPositive = true;
		boolean allNegative = true;
		for(int i = 0; i < 3; i++){
			if(edges[i].pointIsInLeftHalfspace(toTest)){
				allNegative = false;
			}else{
				allPositive = false;
			}
		}
		return allPositive || allNegative;
	}

	@Override
	public AABB calculateAABB() {
		double leastX = corners[0].getX();
		double leastY = corners[0].getY();
		double mostX = leastX;
		double mostY = leastY;
		
		for(int i = 1; i < 3; i++){
			Position currentCorner = corners[i];
			double x = currentCorner.getX();
			double y = currentCorner.getY();
			if(x > mostX) mostX = x;
			if(x < leastX) leastX = x;
			if(y > mostY) mostY = y;
			if(y < leastY) leastY = y;
		}
		
		return new AABB(leastX, leastY, mostX, mostY);
	}

	@Override
	public Shape scale(double scaleFactor) {
		//translate to origin, then scale
		Position newCorner0 = corners[0].subtract(center.getPosition()).scale(scaleFactor).add(center.getPosition());
		Position newCorner1 = corners[1].subtract(center.getPosition()).scale(scaleFactor).add(center.getPosition());
		
		return new Triangle(center, newCorner0, newCorner1);
	}

	public LineSegment[] getEdges() {
		if(edges == null){
			edges = new LineSegment[3];
			edges[0] = new LineSegment(corners[0], corners[1]);
			edges[1] = new LineSegment(corners[1], corners[2]);
			edges[2] = new LineSegment(corners[2], corners[0]);
		}

		return edges;
	}

	public Position[] getVertices() {
		return corners.clone();
	}

}
