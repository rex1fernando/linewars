package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public strictfp class CeaseToExistAfterDelayConfiguration extends AbilityDefinition {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1390001607205214709L;
	
	private static final String name = "Cease to Exist After Delay";
	private static final String description = "Destroys the MapItem that it is attached to after a certain amount of time has elapsed.";
	
	private static final String delayName = "delay";
	private static final Usage delayUsage = Usage.NUMERIC_FLOATING_POINT;
	private static final EditorUsage delayEditorUsage = EditorUsage.PositiveReal;
	private static final String delayDescription = "The amount of time that the MapItem should continue to exist after this Ability is added.";
	
	private static final String deathName = "death";
	private static final Usage deathUsage = Usage.BOOLEAN;
	private static final EditorUsage deathEditorUsage = EditorUsage.Boolean;
	private static final String deathDescription = "Play the death animation.";
	
	static{
		AbilityDefinition.setAbilityConfigMapping(name, CeaseToExistAfterDelayConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public CeaseToExistAfterDelayConfiguration(){
		EditorProperty delay = new EditorProperty(delayUsage, null, delayEditorUsage, delayDescription);
		EditorProperty death = new EditorProperty(deathUsage, null, deathEditorUsage, deathDescription);
		
		this.setPropertyForName(delayName, delay);
		this.setPropertyForName(deathName, death);
	}

	@Override
	public boolean startsActive() {
		return true;
	}

	@Override
	public Ability createAbility(MapItem m) {
		return new CeaseToExistAfterDelay(m);
	}
	
	public strictfp class CeaseToExistAfterDelay implements Ability{
		
		MapItem owner;
		double creationTime;
		private boolean finished = false;
		
		public CeaseToExistAfterDelay(MapItem owner){
			this.owner = owner;
			creationTime = owner.getGameState().getTime();
			
			if(owner instanceof Projectile || owner instanceof Unit){
				
			}else{
				throw new IllegalArgumentException("This Ability can only be added to units and projectiles.");
			}
		}

		@Override
		public void update() {
			if(finished) return;
			
			//first we must compute whether it is time to destroy this mapitem
			double currentTime = owner.getGameState().getTime();
			double delay = (Double) CeaseToExistAfterDelayConfiguration.this.getPropertyForName(delayName).getValue();
			double timeDifference = currentTime - creationTime;
			
			
			if(timeDifference >= delay){
				//it is time, destroy the mapitem
				finished = true;
				
				boolean playDeathAnimation = (Boolean) CeaseToExistAfterDelayConfiguration.this.getPropertyForName(deathName).getValue();
				if(playDeathAnimation){
					owner.setState(MapItemState.Dead);
				}else{
					boolean isProjectile = owner instanceof Projectile;
					if(isProjectile){
						Projectile owningProjectile = (Projectile) owner;
						owningProjectile.getLane().removeProjectile(owningProjectile);
					}else{
						Unit owningUnit = (Unit) owner;
						owningUnit.getWave().remove(owningUnit);
					}
				}
			}
		}

		@Override
		public boolean killable() {
			return true;
		}

		@Override
		public boolean finished() {
			return finished;
		}
		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof CeaseToExistAfterDelayConfiguration)) return false;
		CeaseToExistAfterDelayConfiguration other = (CeaseToExistAfterDelayConfiguration) o;
		if(!other.getPropertyForName(deathName).equals(getPropertyForName(deathName))) return false;
		if(!other.getPropertyForName(delayName).equals(getPropertyForName(delayName))) return false;

		return true;
	}

}
