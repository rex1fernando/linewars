package linewars.gamestate.mapItems;
import java.util.ArrayList;

import linewars.gamestate.Lane;
public class Wave {
	private Lane owner;
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
	
	public Unit[] getUnits()
	{
		return (Unit[])units.toArray();
	}

	public void addUnit(Unit u)
	{
		units.add(u);
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
