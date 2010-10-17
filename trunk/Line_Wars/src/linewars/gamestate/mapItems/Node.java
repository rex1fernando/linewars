package linewars.gamestate.mapItems;

import linewars.gamestate.Player;
import linewars.gamestate.Transformation;

public class Node {
	private Player owner;
	private Player invader;
	private long occupationTime;
	private Lane[] attachedLanes;
	private Building[] containedBuildings;
	private Unit[] containedUnits;
	
	public Player getOwner(){
		return owner;
	}
	
	public boolean isContested(){
		return occupationTime != 0;
	}
	
	public Player getInvader(){
		return invader;
	}
	
	public long getOccupationTime(){
		return occupationTime;
	}
	
	public Building[] getContainedBuildings(){
		return containedBuildings;
	}
	
	public Lane[] getAttachedLanes(){
		return attachedLanes;
	}
	
	public Unit[] getContainedUnits(){
		return containedUnits;
	}
	
	void spawnWaves(){
		if(containedUnits.length != 0){
			
		}
	}
	
	//TODO implement getNextAvailableBuildingSpot
	/**
	 * This method gets the next available position and rotation to build a
	 * building at. Transformation includes a position and
	 * rotation. Returns null if there isn't a spot left to
	 * build any buildings at.
	 * 
	 * @return the next available spot or null
	 */
	public Transformation getNextAvailableBuildingSpot()
	{
		return null;
	}
	
	
	//TODO implement the addBuilding method
	/**
	 * This method attempts to put the input building into the spot
	 * that the building says its in. If that spot is available to
	 * place a building, then it is added and this method returns
	 * true. Otherwise this method returns false.
	 * 
	 * @param b		the building to add to the node
	 * @return		if the building was successfully added
	 */
	public boolean addBuilding(Building b)
	{
		return false;
	}
	
	//TODO implement addUnit
	/**
	 * Adds a unit to the list of contained units in the node
	 * 
	 * @param u		the contained units in the node
	 */
	public void addUnit(Unit u)
	{
		
	}
}
