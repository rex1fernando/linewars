package linewars.gamestate.shapes.collisionStrategies;

import linewars.gamestate.Position;

public strictfp class SeparatingAxisHelper {
	


	/**
	 * 
	 * @param fVertices
	 * The first collection of vertices
	 * @param sVertices
	 * The second collection of vertices
	 * @param axis
	 * @return true iff the two collections of vertices can be separated by a line orthogonal to axis
	 */
	public static boolean separatedByAxis(Position[] fVertices, Position[] sVertices, Position axis){
		double[] fProjectedLocations = new double[fVertices.length];
		double[] sProjectedLocations = new double[sVertices.length];
		
		for(int i = 0; i < fVertices.length; i++){
			fProjectedLocations[i] = fVertices[i].scalarProjection(axis);
		}
		for(int i = 0; i < sVertices.length; i++){
			sProjectedLocations[i] = sVertices[i].scalarProjection(axis);
		}
		
		//now it's time to run the gauntlet
		
		//if this is true then the projected locations of every vertex from r
		//is greater than the projected location of every vertex from t
		boolean fGreater = true;
		//this is true in the converse situation
		boolean sGreater = true;
		//now we determine th truth value of these variables
		//iff we determine both to be false, then the shapes cannot be separated along this axis
		for(double tLocation : fProjectedLocations){
			for(double rLocation : sProjectedLocations){
				if(tLocation > rLocation){
					fGreater = false;
					if(sGreater == false){
						return false;
					}
				}else{
					sGreater = false;
					if(fGreater == false){
						return false;
					}
				}
			}
		}
		
		return true;
	}
}
