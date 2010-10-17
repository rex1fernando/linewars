package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.UnitDefinition;

public class ShootDefinition extends AbilityDefinition {
	
	private ProjectileDefinition ammo = null;
	
	public ShootDefinition(ProjectileDefinition pd, MapItemDefinition owner)
	{
		ammo = pd;
		this.owner = owner;
	}

	@Override
	public boolean checkValidity() {
		return (ammo != null) && (owner instanceof UnitDefinition);
	}

	@Override
	public boolean startsActive() {
		return false;
	}

	@Override
	public Ability createAbility(MapItem m) {
		if(m instanceof Unit)
			return new Shoot(ammo, (Unit)m);
		else
			throw new IllegalArgumentException(m.getName() + " cannot shoot.");
	}

	@Override
	public boolean unlocked() {
		return true;
	}

	@Override
	public String getName() {
		return "Shoot: " + ammo.getName();
	}

	@Override
	public String getDescription() {
		return "Shoots a " + ammo.getName();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof ShootDefinition)
			return ammo.equals(((ShootDefinition)o).ammo);
		else
			return false;
	}

}
