package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.Unit;

/**
 * 
 * @author cschenck
 *
 * This class represents the ability shoot. All it does is spawn
 * a projectile in front of the unit and finish.
 */
public class Shoot implements Ability {
	
	public Shoot(ProjectileDefinition pd, Unit u)
	{
		Transformation t = u.getTransformation();
		//TODO figure out how to move the position of the bullet spawning
		//position out in front of the unit that's shooting it
		Projectile p = pd.createProjectile(t, u.getWave().getLane());
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
