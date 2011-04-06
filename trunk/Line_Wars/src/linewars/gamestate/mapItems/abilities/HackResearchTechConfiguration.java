package linewars.gamestate.mapItems.abilities;

import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.tech.TechConfiguration;

public class HackResearchTechConfiguration extends AbilityDefinition {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8955600672484492961L;

	static {
		AbilityDefinition.setAbilityConfigMapping("Hack Research Tech",
				HackResearchTechConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class HackResearchTech implements Ability
	{
		private MapItem mapItem;
		private double startTime;
		private boolean researched = false;
		
		private HackResearchTech(MapItem m)
		{
			mapItem = m;
			startTime = m.getGameState().getTime();
		}

		@Override
		public void update() {
			if(mapItem.getGameState().getTime() - startTime > getDelay())
			{
				getTechToResearch().research(mapItem.getOwner());
				researched = true;
			}
		}

		@Override
		public boolean killable() {
			return true;
		}

		@Override
		public boolean finished() {
			return researched;
		}
		
	}
	
	private boolean createdAbility = false;
	
	public HackResearchTechConfiguration()
	{
		super.setPropertyForName("tech", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.TechConfig, "The tech to research"));
		super.setPropertyForName("delay", new EditorProperty(Usage.NUMERIC_FLOATING_POINT,
				null, EditorUsage.PositiveReal, "The time after the first unit is spawned to research the tech"));
	}
	
	private TechConfiguration getTechToResearch()
	{
		return (TechConfiguration)super.getPropertyForName("tech").getValue();
	}
	
	private double getDelay()
	{
		return (Double)super.getPropertyForName("delay").getValue();
	}

	@Override
	public boolean startsActive() {
		return true;
	}

	@Override
	public Ability createAbility(MapItem m) {
		if(createdAbility)
		{
			return new Ability() {
				public void update() {}
				public boolean killable() {return true;}
				public boolean finished() {return true;}
			};
		}
		else
		{
			createdAbility = true;
			return new HackResearchTech(m);
		}
	}

	@Override
	public String getName() {
		return "Hack research tech";
	}

	@Override
	public String getDescription() {
		return "Researches a tech after a specified amount of seconds";
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof HackResearchTechConfiguration) &&
				((HackResearchTechConfiguration) o).getDelay() == getDelay() &&
				((HackResearchTechConfiguration) o).getTechToResearch().equals(getTechToResearch());
	}

}
