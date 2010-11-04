package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.Function;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.tech.Tech;


/**
 * 
 * @author cschenck
 *
 * This class is the definition of the research tech ability. It knows
 * what tech to research, how many times that tech has been researched,
 * how long it takes to research the tech, and a cost function for each
 * time the tech is researched. Handles creating dummy abilities (abilities
 * that do nothing) if the tech can't be researched any more or if the
 * player doesn't have enough stuff to pay for it.
 */
public class ResearchTechDefinition extends AbilityDefinition {
	
	private Tech tech = null;
	private int numberOfTimesResearched = 0;
	private Function costFunction = null;

	
	public ResearchTechDefinition(Tech t, MapItemDefinition owner, int ID)
	{
		super(ID);
		tech = t;
		this.owner = owner;
		costFunction = tech.getCostFunction();
	}

	@Override
	public boolean startsActive() {
		return false;
	}
	@Override
	public Ability createAbility(MapItem m) {
		if(numberOfTimesResearched + 1 <= tech.maxTimesResearchable())
		{
			if(owner.getOwner().getStuff() >= costFunction.f(numberOfTimesResearched + 1))
			{
				owner.getOwner().spendStuff(costFunction.f(++numberOfTimesResearched));
				return new ResearchTech(tech, false);
			}
			else
				return new ResearchTech(tech, true);
		}
		else
			return null;
	}

	@Override
	public boolean unlocked() {
		return tech.researchable() && numberOfTimesResearched < tech.maxTimesResearchable();
	}

	@Override
	public String getName() {
		return "Research Tech: " + tech.getName();
	}

	@Override
	public String getDescription() {
		return tech.getDescription();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof ResearchTechDefinition)
			return tech.equals(((ResearchTechDefinition)o).tech);
		else
			return false;
	}

	@Override
	public boolean checkValidity() {
		return (this.owner instanceof BuildingDefinition);
	}

	@Override
	public String getIconURI() {
		return tech.getIconURI();
	}

	@Override
	public String getPressedIconURI() {
		return tech.getPressedIconURI();
	}

	@Override
	public String getRolloverIconURI() {
		return tech.getRolloverIconURI();
	}

	@Override
	public String getSelectedIconURI() {
		return tech.getSelectedIconURI();
	}

}
