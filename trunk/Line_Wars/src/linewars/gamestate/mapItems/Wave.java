package linewars.gamestate.mapItems;
import java.util.ArrayList;

import linewars.gamestate.Lane;
import linewars.gamestate.Player;
public class Wave {
	private Lane owner;
	private Node origin;
	ArrayList<Unit> units;
	
	/**
	 * gets the lane that owns this wave
	 * 
	 * @return	the owning lane
	 */
	public Lane getLane()
	{
		return owner;
	}
	
	/**
	 * Gets the Node that 
	 * @return
	 */
	public Node getOrigin()
	{
		return origin;
	}
	
	public Wave(Lane owner, Unit u)
	{
		this.owner = owner;
		units = new ArrayList<Unit>();
		units.add(u);
	}
	
	public Wave(Lane owner)
	{
		this.owner = owner;
		units = new ArrayList<Unit>();
	}
	
	public Unit[] getUnits()
	{
		return (Unit[])units.toArray();
	}

	public boolean addUnit(Unit u)
	{
		//Check if the wave is empty and if it's not, make sure the unit you're trying to add belongs to the same player as the wave.
		if(!units.isEmpty() && u.getOwner().getPlayerColor() != units.get(0).getOwner().getPlayerColor()){
			return false;
		}
		return units.add(u);
	}
	
	public void addUnits(Unit[] u)
	{
		for(int i = 0; i < u.length; i++)
		{
			this.addUnit(u[i]);
		}
	}
	
	/**
	 * Gets the position of the wave within the lane.
	 * @return a double that represents the percentage of the lane that is between the wave and p0 in the lane.
	 */
	public double getPosition()
	{
		//TODO implement this method
		return 0.5;
	}
	
	
}
