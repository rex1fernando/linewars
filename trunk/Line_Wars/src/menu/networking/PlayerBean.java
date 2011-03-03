package menu.networking;

import java.awt.Color;


public class PlayerBean
{
	private String name;
	private int slot;
	private Object race;
	private Color color;
	
	public PlayerBean(String n, Color c, int s, Object r)
	{
		name = n;
		color = c;
		slot = s;
		race = r;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSlot() {
		return slot;
	}
	public void setSlot(int slot) {
		this.slot = slot;
	}
	public Object getRace() {
		return race;
	}
	public void setRace(Object race) {
		this.race = race;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
}