package linewars.gamestate.mapItems.strategies.turret;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.Turret;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.shapes.AABB;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class MechTurretStrategyConfiguration extends TurretStrategyConfiguration {
	
	private static final String name = "Mech Turret Strategy";
	
	static{
		StrategyConfiguration.setStrategyConfigMapping(name, MechTurretStrategyConfiguration.class, AbilityStrategyEditor.class);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2559039466303153979L;
	
	//the maximum distance that this can shoot
	private static final String rangeName = "range";
	private static final Usage rangeUsage = Usage.NUMERIC_FLOATING_POINT;
	private static final EditorUsage rangeEditorUsage = EditorUsage.PositiveReal;
	private static final String rangeDescription = "The maximum distance that this Turret can consider targets.";
	private static final EditorProperty rangeProperty = new EditorProperty(rangeUsage, null, rangeEditorUsage, rangeDescription);
	
	//the minimum distance that it would like to be at from its target
	private static final String minRangeName = "minimumRange";
	private static final Usage minRangeUsage = Usage.NUMERIC_FLOATING_POINT;
	private static final EditorUsage minRangeEditorUsage = EditorUsage.PositiveReal;
	private static final String minRangeDescription = "The minimum distance that this Turret would like to maintain from its target. Note that it can still shoot at things closer than this.";
	private static final EditorProperty minRangeProperty = new EditorProperty(minRangeUsage, null, minRangeEditorUsage, minRangeDescription);
	
	//the type of projectile it shoots
	private static final String projectileName = "projectile";
	private static final Usage projectileUsage = Usage.CONFIGURATION;
	private static final EditorUsage projectileEditorUsage = EditorUsage.ProjectileConfig;
	private static final String projectileDescription = "The Configuration of the Projectile that this TurretStrategy should shoot.";
	private static final EditorProperty projectileProperty = new EditorProperty(projectileUsage, null, projectileEditorUsage, projectileDescription);
	

	public MechTurretStrategyConfiguration(){
		this.setPropertyForName(rangeName, rangeProperty);
		this.setPropertyForName(minRangeName, minRangeProperty);
		this.setPropertyForName(projectileName, projectileProperty);
	}
	
	@Override
	public TurretStrategy createStrategy(MapItem m) {
		return new MechTurretStrategy((Turret) m);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof MechTurretStrategyConfiguration)) return false;
		MechTurretStrategyConfiguration other = (MechTurretStrategyConfiguration) obj;
		
		//if NPEs are thrown, check whether these names are defined for both of them first
		if(!this.getPropertyForName(rangeName).equals(other.getPropertyForName(rangeName))) return false;
		if(!this.getPropertyForName(minRangeName).equals(other.getPropertyForName(minRangeName))) return false;
		if(!this.getPropertyForName(projectileName).equals(other.getPropertyForName(projectileName))) return false;
		return true;
	}
	
	public class MechTurretStrategy implements MinimumRangeTurretStrategy{
		
		Turret owner;
		Position target;

		public MechTurretStrategy(Turret m) {
			owner = m;
			target = null;
		}

		@Override
		public double getRange() {
			return (Double) MechTurretStrategyConfiguration.this.getPropertyForName(rangeName).getValue();
		}

		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			if(target == null){
				throw new IllegalStateException("fight() was called for MechTurretStrategy, but no target was defined!");
			}
			
			//create a Projectile at this location pointed in the direction of target
			ProjectileDefinition projectile = (ProjectileDefinition) MechTurretStrategyConfiguration.this.getPropertyForName(projectileName).getValue();
			Position thisLocation = owner.getPosition();
			Position relativeTarget = target.subtract(thisLocation);
			double direction = relativeTarget.getAngle();
			
			System.out.println(projectile.getName());
			fireLaser(projectile, target, thisLocation, direction);
			//projectile.createMapItem(new Transformation(thisLocation, direction), owner.getOwner(), owner.getGameState());
			
		}
		
		private void fireLaser(ProjectileDefinition laserPiece, Position target, Position currentLocation, double direction){
			AABB bounds = laserPiece.getBodyConfig().construct(new Transformation(target, 0)).calculateAABB();
			double pieceLength = bounds.getXMax() - bounds.getXMin();
			
			Position increment = new Position(pieceLength, 0).rotateAboutPosition(Position.ORIGIN, direction);
			Position relativeTarget = target.subtract(currentLocation);
			double laserLength = relativeTarget.length();
			int numLasers = (int) ((laserLength + 0.5) / pieceLength);
			
			for(int i = 0; i < numLasers; i++){
				Position creationPosition = currentLocation.add(increment.scale(i + 0.5));
				Transformation creationLocation = new Transformation(creationPosition, direction);
				laserPiece.createMapItem(creationLocation, owner.getOwner(), owner.getGameState());
			}
			
			//Place an endcap at the end of the laser
			Position endcapPosition = currentLocation.add(increment.scale(numLasers));
			Projectile endcap = laserPiece.createMapItem(new Transformation(endcapPosition, direction), owner.getOwner(), owner.getGameState());
			endcap.setState(MapItemState.Firing);
			
			
		}

		@Override
		public String name() {
			return "Mech Turret Strategy";
		}

		@Override
		public TurretStrategyConfiguration getConfig() {
			return MechTurretStrategyConfiguration.this;
		}

		@Override
		public double getMinimumRange() {
			return (Double) MechTurretStrategyConfiguration.this.getPropertyForName(minRangeName).getValue();
		}

		@Override
		public void setTarget(Position target) {
			this.target = target;			
		}
		
	}
}
