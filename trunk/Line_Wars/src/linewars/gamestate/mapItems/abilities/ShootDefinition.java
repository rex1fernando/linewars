package linewars.gamestate.mapItems.abilities;

import java.util.Observable;
import java.util.Observer;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.Unit;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

/**
 * 
 * @author , Connor Schenck
 *
 * this class represents the definition of the shoot ability.
 * It knows what projectile definition to use and the maximum
 * range away this ability can be used.
 */
public strictfp class ShootDefinition extends AbilityDefinition implements Observer {
	
	static {
		AbilityDefinition.setAbilityConfigMapping("Shoot", ShootDefinition.class, AbilityStrategyEditor.class);
	}
	
	private ProjectileDefinition ammo = null;
	private double range;
	
	public strictfp class Shoot implements Ability {
		
		private Shoot(Unit u)
		{
			Transformation t = u.getTransformation();
			//TODO figure out how to move the position of the bullet spawning
			//position out in front of the unit that's shooting it
			Projectile p = ammo.createMapItem(t, u.getOwner(), u.getGameState());
			u.getWave().getLane().addProjectile(p);
		}

		@Override
		public void update() {}

		@Override
		public boolean killable() {
			return true;
		}

		@Override
		public boolean finished() {
			return true;
		}

	}
	
	public ShootDefinition()
	{
		super.setPropertyForName("ammo", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.ProjectileConfig, "The Projectile that is shot"));
		super.setPropertyForName("range", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The range of this shoot ability"));
		super.addObserver(this);
	}

	@Override
	public boolean startsActive() {
		return false;
	}

	@Override
	public Ability createAbility(MapItem m) {
		if(m instanceof Unit)
			return new Shoot((Unit)m);
		else
			throw new IllegalArgumentException(m.getName() + " cannot shoot.");
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
	
	public double getRange()
	{
		return range;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == this)
		{
			if(arg.equals("ammo"))
				ammo = (ProjectileDefinition)super.getPropertyForName("ammo").getValue();
			if(arg.equals("range") && super.getPropertyForName("range").getValue() != null)
				range = (Double)super.getPropertyForName("range").getValue();
		}
	}

}
