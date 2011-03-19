package menu.networking;

import java.awt.Color;
import java.io.Serializable;

import linewars.gamestate.Race;


public class PlayerBean implements Serializable
{
	private static final long serialVersionUID = -7857078559659192694L;
	private String name;
	private int slot;
	private Race race;
	private Color color;
	
	public PlayerBean(String n, Color c, int s, Race r) {
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
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PlayerBean)) return false;
		
		PlayerBean p = (PlayerBean) o;
		return color.equals(p.color)
			&& name.equals(p.name)
			&& slot == p.slot
			&& race.equals(p.race);
	}
	@Override
	public int hashCode() {
		return name.hashCode() * 7 +
			color.hashCode() * 23 +
			slot * 41 +
			race.hashCode() * 57;
	}
	
	public PlayerBean copy()
	{
		PlayerBean ret = new PlayerBean(name, color, slot, race);
		return ret;
	}
}