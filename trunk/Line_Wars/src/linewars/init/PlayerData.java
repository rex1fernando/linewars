package linewars.init;

import java.awt.Color;

import linewars.gamestate.Race;

public class PlayerData {
	private String name;
	private Race race;
	private Color color;
	private int startingSlot;
	
	//ip address
	//observer? y/n
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Race getRace() {
		return race;
	}
	
	public void setRace(Race race) {
		this.race = race;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public int getStartingSlot() {
		return startingSlot;
	}
	
	public void setStartingSlot(int startingSlot) {
		this.startingSlot = startingSlot;
	}	
}
