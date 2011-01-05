package linewars.gamestate.mapItems;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.turret.TurretStrategy;

public class Turret extends MapItem {
	
	private TurretDefinition def;
	private TurretStrategy turretStrat;
	private CollisionStrategy colStrat;

	public Turret(Transformation trans, TurretDefinition def, TurretStrategy ts) {
		super(trans, def);
		this.def = def;
		turretStrat = ts.copy();
		turretStrat.setTurret(this);
		colStrat = def.getCollisionStrategy().createInstanceOf(this);
	}

	@Override
	public MapItemDefinition<? extends MapItem> getDefinition() {
		return def;
	}

	@Override
	public CollisionStrategy getCollisionStrategy() {
		return colStrat;
	}
	
	public TurretStrategy getTurretStrategy()
	{
		return turretStrat;
	}

}
