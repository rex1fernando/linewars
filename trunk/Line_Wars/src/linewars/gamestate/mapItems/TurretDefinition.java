package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.turret.TurretStrategy;

public class TurretDefinition extends MapItemDefinition<Turret> {
	
	private TurretStrategy turretStrat;

	public TurretDefinition(String URI, Player owner,
			GameState gameState) throws FileNotFoundException,
			InvalidConfigFileException {
		super(URI, owner, gameState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Turret createMapItem(Transformation t) {
		return new Turret(t, this, turretStrat);
	}

	@Override
	protected void forceSubclassReloadConfigData() {
		//TODO load the turret strat
		
	}

}
