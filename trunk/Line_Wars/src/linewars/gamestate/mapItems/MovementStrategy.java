package linewars.gamestate.mapItems;

public interface MovementStrategy {
	
	public void setUnit(Unit u);
	
	public MovementStrategy copy();

}
