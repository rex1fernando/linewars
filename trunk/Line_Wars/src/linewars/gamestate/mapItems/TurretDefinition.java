package linewars.gamestate.mapItems;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.turret.TurretStrategyConfiguration;
import configuration.Property;
import configuration.Usage;

public class TurretDefinition extends MapItemDefinition<Turret> {
	
	private TurretStrategyConfiguration turretStrat;

	public TurretDefinition() {
		super();
		super.setPropertyForName("turretStrat", new Property(Usage.CONFIGURATION));
	}

	@Override
	public Turret createMapItem(Transformation t, Player owner, GameState gameState) {
		return new Turret(t, this, owner, gameState);
	}

	@Override
	protected void forceSubclassReloadConfigData() {
		turretStrat = (TurretStrategyConfiguration)super.getPropertyForName("turretStrat").getValue();
	}
	
	public TurretStrategyConfiguration getTurretStratConfig()
	{
		return turretStrat;
	}

}
