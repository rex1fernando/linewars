package linewars.test;

import java.util.List;

public class TestVariables {

	/*
	 * Author: Ryan Frahm
	 * This is used to limit the reinvention of the wheel
	 * All it does is name some common variables that a bunch of classes use.
	 * This saves me time. :)
	 */
	private String mapURI;
	private int numPlayers;
	private List<String> raceURIs;
	private List<String> players;
	
	public String getMapURI()
	{
		return mapURI = "resources/display/Game_map.png";
	}
	
	public int getNumPlayers()
	{
		return numPlayers = 2;
	}
	
	public List<String> getRaceURIs()
	{
		raceURIs.add("resources/races/thatOneRace.cfg");
		return raceURIs;
	}
	
	public List<String> getPlayers()
	{
		players.add("Joe");
		players.add("Ryan");
		return players;
	}
}
