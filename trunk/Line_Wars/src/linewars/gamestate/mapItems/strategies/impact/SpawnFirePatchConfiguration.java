package linewars.gamestate.mapItems.strategies.impact;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public strictfp class SpawnFirePatchConfiguration extends ImpactStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3941705048666440539L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Spawn Fire Patch",
				SpawnFirePatchConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public strictfp class SpawnFirePatch implements ImpactStrategy
	{
		
		private Projectile proj;
		
		private SpawnFirePatch(Projectile p)
		{
			proj = p;
		}

		@Override
		public String name() {
			return "Spawn a fire patch upon collision";
		}

		@Override
		public ImpactStrategyConfiguration getConfig() {
			return SpawnFirePatchConfiguration.this;
		}

		@Override
		public void handleImpact(MapItem m) {
			Projectile firePatch = getFirePatch().createMapItem(proj.getTransformation(), 
					proj.getOwner(), proj.getGameState());
			firePatch.getModifier().pushUnderStack(proj.getModifier());
			proj.getLane().addProjectile(firePatch);
			proj.setState(MapItemState.Dead);
			proj.setDurability(0);
		}

		@Override
		public void handleImpact(Position p) {
			Projectile firePatch = getFirePatch().createMapItem(proj.getTransformation(), 
					proj.getOwner(), proj.getGameState());
			firePatch.getModifier().pushUnderStack(proj.getModifier());
			proj.getLane().addProjectile(firePatch);
			proj.setState(MapItemState.Dead);
			proj.setDurability(0);
		}
		
	}
	
	public SpawnFirePatchConfiguration()
	{
		super.setPropertyForName("firePatch", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.ProjectileConfig, "The fire patch to place on the ground upon impact"));
	}
	
	private ProjectileDefinition getFirePatch()
	{
		return (ProjectileDefinition)super.getPropertyForName("firePatch").getValue();
	}

	@Override
	public ImpactStrategy createStrategy(MapItem m) {
		return new SpawnFirePatch((Projectile) m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof SpawnFirePatchConfiguration) &&
				((SpawnFirePatchConfiguration) obj).getFirePatch().equals(getFirePatch());
	}

}
