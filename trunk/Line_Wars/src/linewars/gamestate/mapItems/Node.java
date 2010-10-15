package linewars.gamestate.mapItems;

import linewars.gamestate.Player;

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
}
