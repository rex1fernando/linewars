package linewars.gamestate.mapItems;
import java.util.ArrayList;
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
	
	
}
