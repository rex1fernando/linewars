package menu.networking;

import java.awt.Color;
import java.io.Serializable;


public class PlayerBean implements Serializable
{
	private static final long serialVersionUID = 4002707351260107011L;
	private String name;
	private int slot;
	private String race;
	private Color color;
	
	public PlayerBean(String n, Color c, int s, String r)
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
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	@Override
	public boolean equals(Object o) {
		// implement
		return o == this;
	}
	@Override
	public int hashCode() {
		// implement
		return 0;
	}
}