package linewars.gamestate.mapItems.strategies.movement;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.strategies.combat.DroneCombatStrategy;
import utility.Observable;
import utility.Observer;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class DroneMovementConfiguration extends MovementStrategyConfiguration implements Observer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4567252065436587794L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Drone Movement",
				DroneMovementConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private static final double DISTANCE_TO_CARRIER_THRESHOLD = 1.0;
	
	private double speed;
	
	public class DroneMovement implements MovementStrategy
	{

		private Unit unit;
		private Transformation target;
		
		private DroneMovement(Unit u)
		{
			unit = u;
		}
		
		@Override
		public String name() {
			return "Drone movement strategy for " + unit.getName();
		}

		@Override
		public MovementStrategyConfiguration getConfig() {
			return DroneMovementConfiguration.this;
		}

		@Override
		public double setTarget(Transformation t) {
			target = t;
			return 1;
		}

		@Override
		public void move() {
			//if the drone is in combat, go to target
			//else go back to the carrier
			DroneCombatStrategy dcs = (DroneCombatStrategy) unit.getCombatStrategy();
			if(dcs.getLastFightTick() != unit.getGameState().getTimerTick())
				target = dcs.getDroneCarrier().getTransformation();
			
			if(target == null)
				return;
			
			//first rotate to face the correct direction
			Position change = target.getPosition().subtract(unit.getPosition());
			double rot = change.getAngle();
			
			//now scale the change by the max moveable distance as determined by the speed
			double maxDist = unit.getModifier().getModifier(MapItemModifiers.moveSpeed)*
							speed*unit.getGameState().getLastLoopTime();
			if(change.dot(change) > maxDist*maxDist) //squaring maxDist rather than square rooting
				change = change.normalize().scale(maxDist);
			
			//set the position of the drone
			unit.setTransformation(new Transformation(unit.getPosition().add(change), rot));
			target = null;
			
			//if the drone is not in combat, check to see if it needs to re-enter its carrier
			if(dcs.getLastFightTick() != unit.getGameState().getTimerTick() && 
					unit.getPosition().distanceSquared(dcs.getDroneCarrier().getPosition()) <= DISTANCE_TO_CARRIER_THRESHOLD)
			{
				unit.getWave().remove(unit);
			}
		}
		
	}
	
	public DroneMovementConfiguration()
	{
		super.setPropertyForName("speed", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, 0, EditorUsage.PositiveReal,
				"The speed of the projectile per second"));
		super.addObserver(this);
	}

	@Override
	public MovementStrategy createStrategy(MapItem m) {
		if(m instanceof Unit)
			return new DroneMovement((Unit) m);
		else
			throw new IllegalArgumentException("Only units may have movement strategies");
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof DroneMovementConfiguration) &&
				(((DroneMovementConfiguration)obj).speed == speed);
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == this && arg.equals("speed"))
			speed = (Double)super.getPropertyForName("speed").getValue();
	}

}
