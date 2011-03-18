package linewars.gamestate.mapItems;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.turret.TurretStrategy;

public class Turret extends MapItem {
	
	private TurretDefinition def;
	private TurretStrategy turretStrat;

	public Turret(Transformation trans, TurretDefinition def, Player owner, GameState gameState) {
		super(trans, def, owner, gameState);
		this.def = def;
		turretStrat = def.getTurretStratConfig().createStrategy(this);
	}

	@Override
	public MapItemDefinition<? extends MapItem> getDefinition() {
		return def;
	}
	
	public TurretStrategy getTurretStrategy()
	{
		return turretStrat;
	}

	@Override
	protected void setDefinition(MapItemDefinition<? extends MapItem> def) {
		this.def = (TurretDefinition) def;
	}

}
