package linewars.gamestate.mapItems.strategies.combat;

import java.util.HashMap;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.UnitDefinition;
import utility.Observable;
import utility.Observer;
import configuration.Usage;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class SpawnDronesConfiguration extends
		CombatStrategyConfiguration implements Observer {
	
	private double range;
	private int numDrones;
	private double launchTime;
	private UnitDefinition droneConfig;
	
	public class SpawnDrones implements CombatStrategy
	{
		
		private Unit droneCarrier;
		private HashMap<Unit, Unit> dronesToTargets = new HashMap<Unit, Unit>();
		private double lastLaunchTime = 0;
		
		private SpawnDrones(Unit u)
		{
			droneCarrier = u;
		}

		@Override
		public String name() {
			return droneCarrier.getName() + " spawns healing drones";
		}

		@Override
		public CombatStrategyConfiguration getConfig() {
			return SpawnDronesConfiguration.this;
		}

		@Override
		public double getRange() {
			return range;
		}

		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			// TODO Auto-generated method stub
			compile error
			//next check to see if another drone can be spawned
			//next check to see if any of the current drones have finished their target
			//-->how do you do this? it's different for damage, healing
			//next give targets to drones that need targets
			//-->how do you do this? it's different for damage, healing
			//next tell the drones to fight
			
		}
		
	}
	
	public SpawnDronesConfiguration()
	{
		super.setPropertyForName("range", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, 0, EditorUsage.PositiveReal,
				"The maximum range to launch drones at"));
		super.setPropertyForName("numDrones", new EditorProperty(
				Usage.NUMERIC_INTEGER, 0, EditorUsage.NaturalNumber,
				"The number of drones to spawn"));
		super.setPropertyForName("launchTime", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, 0, EditorUsage.PositiveReal,
				"The time it takes to launch a drone in seconds"));
		super.setPropertyForName("droneConfig", new EditorProperty(
				Usage.CONFIGURATION, null, EditorUsage.UnitConfig,
				"The config for the drones to spawn"));
	}

	@Override
	public CombatStrategy createStrategy(MapItem m) {
		if(m instanceof Unit)
			return new SpawnDrones((Unit) m);
		else
			throw new IllegalArgumentException("Only units may have combat strategies");
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SpawnDronesConfiguration)
		{
			SpawnDronesConfiguration shdc = (SpawnDronesConfiguration) obj;
			return shdc.droneConfig.equals(droneConfig) &&
					shdc.launchTime == launchTime &&
					shdc.numDrones == numDrones &&
					shdc.range == range;
		}
		else
			return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == this)
		{ 
			if(arg.equals("range"))
				range = (Double)super.getPropertyForName("range").getValue();
			else if(arg.equals("numDrones"))
				numDrones = (Integer)super.getPropertyForName("numDrones").getValue();
			else if(arg.equals("launchTime"))
				launchTime = (Double)super.getPropertyForName("launchTime").getValue();
			else if(arg.equals("droneConfig"))
				droneConfig = (UnitDefinition)super.getPropertyForName("droneConfig").getValue();
				
		}
	}

}
