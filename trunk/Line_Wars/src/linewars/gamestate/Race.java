package linewars.gamestate;

import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.GateDefinition;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.gamestate.tech.TechConfiguration;
import configuration.Configuration;
import configuration.ListConfiguration;
import configuration.Property;
import configuration.Usage;


/**
 * 
 * @author John George
 *
 *	This class represents a race, of which each player has one. Races know what units and buildings are available to be 
 *  created by itself.
 */
public strictfp class Race extends Configuration {
	
	/**
	 * Creates a new Race object based on the information in the given ConfigData.
	 * @param p
	 * 		The ConfigData object to be used to create this Race.
	 */
	public Race()
	{
		super.setPropertyForName("name", new Property(Usage.STRING));
		super.setPropertyForName("units", new Property(Usage.CONFIGURATION, 
				new ListConfiguration<UnitDefinition>(new ArrayList<UnitDefinition>(), 
						new ArrayList<String>(), new ArrayList<Usage>())));
		super.setPropertyForName("techs", new Property(Usage.CONFIGURATION, 
				new ListConfiguration<TechConfiguration>(new ArrayList<TechConfiguration>(), 
						new ArrayList<String>(), new ArrayList<Usage>())));
		super.setPropertyForName("buildings", new Property(Usage.CONFIGURATION, 
				new ListConfiguration<BuildingDefinition>(new ArrayList<BuildingDefinition>(), 
						new ArrayList<String>(), new ArrayList<Usage>())));
		super.setPropertyForName("commandCenter", new Property(Usage.CONFIGURATION));
		super.setPropertyForName("gate", new Property(Usage.CONFIGURATION));
	}
	
	/**
	 * 
	 * @return
	 * 		A List containing all of the BuildingURIs associated with this Race.
	 */
	public List<BuildingDefinition> getBuildings()
	{
		return (List<BuildingDefinition>) super.getPropertyForName("buildings").getValue();
	}

	/**
	 * 
	 * @return
	 * 		A List containing all of the UnitURIs associated with this Race.
	 */
	public List<UnitDefinition> getUnits()
	{
		return (List<UnitDefinition>) super.getPropertyForName("units").getValue();
	}
	
	/**
	 * 
	 * @return
	 * 		A List containing all of the TechURIs associated with this Race.
	 */
	public List<TechConfiguration> getTechs()
	{
		return (List<TechConfiguration>) super.getPropertyForName("techs").getValue();
	}
	
	/**
	 * 
	 * @return
	 * 		The CommandCenterURI associated with this Race.
	 */
	public BuildingDefinition getCommandCenter()
	{
		return (BuildingDefinition) super.getPropertyForName("commandCenter").getValue();
	}
	
	/**
	 * 
	 * @return
	 * 		The name of this Race.
	 */
	public String getName()
	{
		return (String)super.getPropertyForName("name").getValue();
	}
	
	/**
	 * 
	 * @return
	 * 		The GateURI associated with this Race.
	 */
	public GateDefinition getGate()
	{
		return (GateDefinition) super.getPropertyForName("gate").getValue();
	}

}
