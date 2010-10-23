package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.Function;
import linewars.gamestate.Tech;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.parser.Parser;
import linewars.parser.ParserKeys;

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
	private long researchTime;
	
	public ResearchTechDefinition(Tech t, MapItemDefinition owner)
	{
		tech = t;
		this.owner = owner;
		costFunction = new Function(tech.getParser().getParser(ParserKeys.costFunction));
		try {
			researchTime = (long) tech.getParser().getNumericValue(ParserKeys.researchTime);
		} catch(Parser.NoSuchKeyException e) {
			//allow the research time to be unspecified since we haven't discussed it
			researchTime = 0;
		}
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
				return new ResearchTech(tech, researchTime, false);
			}
			else
				return new ResearchTech(tech, researchTime, true);
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
		return tech.getParser().getStringValue(ParserKeys.icon);
	}

}
