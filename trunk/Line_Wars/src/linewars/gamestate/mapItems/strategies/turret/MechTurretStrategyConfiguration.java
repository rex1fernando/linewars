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
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public strictfp class MechTurretStrategyConfiguration extends TurretStrategyConfiguration {
	
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
	private EditorProperty rangeProperty = new EditorProperty(rangeUsage, null, rangeEditorUsage, rangeDescription);
	
	//the minimum distance that it would like to be at from its target
	private static final String minRangeName = "minimumRange";
	private static final Usage minRangeUsage = Usage.NUMERIC_FLOATING_POINT;
	private static final EditorUsage minRangeEditorUsage = EditorUsage.PositiveReal;
	private static final String minRangeDescription = "The minimum distance that this Turret would like to maintain from its target. Note that it can still shoot at things closer than this.";
	private EditorProperty minRangeProperty = new EditorProperty(minRangeUsage, null, minRangeEditorUsage, minRangeDescription);
	
	//the type of projectile it shoots
	private static final String projectileName = "projectile";
	private static final Usage projectileUsage = Usage.CONFIGURATION;
	private static final EditorUsage projectileEditorUsage = EditorUsage.ProjectileConfig;
	private static final String projectileDescription = "The Configuration of the Projectile that this TurretStrategy should shoot.";
	private EditorProperty projectileProperty = new EditorProperty(projectileUsage, null, projectileEditorUsage, projectileDescription);
	
	//the proportion of the width of the Mech that this turret is offset by... ranges f
	private static final String offsetName = "offset";
	private static final Usage offsetUsage = Usage.NUMERIC_FLOATING_POINT;
	private static final EditorUsage offsetEditorUsage = EditorUsage.Real;
	private static final String offsetDescription = "A real-valued offset used to compute where the projectiles are spawned.\n" +
			"If it is set to 0, projectiles will be spawned immediately in front of the Turret.\n" +
			"If it is set to 1, projectiles will be spawned on the far right edge of the Turret.\n" +
			"If it is set to -1, projectiles will be spawned on the far left edge of the Turret.";
	private EditorProperty offsetProperty = new EditorProperty(offsetUsage, null, offsetEditorUsage, offsetDescription);
	
	public MechTurretStrategyConfiguration(){
		this.setPropertyForName(rangeName, rangeProperty);
		this.setPropertyForName(minRangeName, minRangeProperty);
		this.setPropertyForName(projectileName, projectileProperty);
		this.setPropertyForName(offsetName, offsetProperty);
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
	
	public strictfp class MechTurretStrategy implements MinimumRangeTurretStrategy{
		
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
			
			//we need to compute a forward offset so that the projectile is spawned in front of the turret, not in the turret
			double turretLength = owner.getBody().boundingRectangle().getWidth() / 2;
			Position turretOffset = Position.getUnitVector(direction).scale(turretLength);
			Position spawnAt = thisLocation.add(turretOffset);
			
			//now we must also compute a sideways offset because this turret probably has to do the weird rotate-y shit
			double turretWidth = owner.getBody().boundingRectangle().getHeight() / 2;
			double horizontalOffsetRatio = (Double) MechTurretStrategyConfiguration.this.getPropertyForName(offsetName).getValue();
			double horizontalOffset = turretWidth * horizontalOffsetRatio;
			turretOffset = Position.getUnitVector(direction + Math.PI / 2).scale(horizontalOffset);
			spawnAt = spawnAt.add(turretOffset);
			
			Projectile spawnedProjectile = projectile.createMapItem(new Transformation(spawnAt, direction), owner.getOwner(), owner.getGameState());
			owner.getWave().getLane().addProjectile(spawnedProjectile);
			
			spawnedProjectile.getModifier().pushUnderStack(owner.getModifier());
			
			owner.setState(MapItemState.Firing);
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
