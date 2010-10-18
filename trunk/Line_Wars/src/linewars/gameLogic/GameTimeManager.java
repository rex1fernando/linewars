package linewars.gameLogic;

//TODO this is a temp class
public class GameTimeManager {

	private static int currentTick;
	private static final int MILLIS_PER_TICK = 100;
	
	public static void startGame()
	{
		currentTick = 0;
	}
	
	public static long currentTimeMillis()
	{
		return currentTick*MILLIS_PER_TICK;
	}
	
	public static int currentTick()
	{
		return currentTick;
	}
	
	public static void incrementTick()
	{
		currentTick++;
	}
	
}
