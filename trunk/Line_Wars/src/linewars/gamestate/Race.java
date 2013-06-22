package linewars.gamestate;

import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.GateDefinition;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.gamestate.playerabilities.PlayerAbility;
import linewars.gamestate.tech.TechGraph;
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
	 * 
	 */
	private static final long serialVersionUID = 3751813555858676197L;

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
		super.setPropertyForName("buildings", new Property(Usage.CONFIGURATION, 
				new ListConfiguration<BuildingDefinition>(new ArrayList<BuildingDefinition>(), 
						new ArrayList<String>(), new ArrayList<Usage>())));
		super.setPropertyForName("techs", new Property(Usage.CONFIGURATION, 
				new ListConfiguration<TechGraph>(new ArrayList<TechGraph>(), 
						new ArrayList<String>(), new ArrayList<Usage>())));
		super.setPropertyForName("playerAbilities", new Property(Usage.CONFIGURATION, 
				new ListConfiguration<PlayerAbility>(new ArrayList<PlayerAbility>(), 
						new ArrayList<String>(), new ArrayList<Usage>())));
		super.setPropertyForName("commandCenter", new Property(Usage.CONFIGURATION));
		super.setPropertyForName("gate", new Property(Usage.CONFIGURATION));
	}
	
	public void setName(String newName)
	{
		super.setPropertyForName("name", new Property(Usage.STRING, newName));
	}
	
	@SuppressWarnings("unchecked")
	public void addUnit(UnitDefinition ud, boolean enabled)
	{
		ArrayList<UnitDefinition> units = ((ListConfiguration<UnitDefinition>)super.getPropertyForName("units").getValue()).getFullList();		
		ArrayList<Boolean> enabledList = ((ListConfiguration<UnitDefinition>)super.getPropertyForName("units").getValue()).getEnabledFlags();		
		ArrayList<Usage> usages = ((ListConfiguration<UnitDefinition>)super.getPropertyForName("units").getValue()).getUsages();
		ArrayList<String> names = ((ListConfiguration<UnitDefinition>)super.getPropertyForName("units").getValue()).getNames();
		
		units.add(ud);
		enabledList.add(enabled);
		usages.add(Usage.CONFIGURATION);
		String name = (String) ud.getPropertyForName("bfgName").getValue();
		while(names.contains(name))
			name += "_";
		names.add(name);
		
		super.setPropertyForName("units", new Property(Usage.CONFIGURATION, new ListConfiguration<UnitDefinition>(units, names, usages, enabledList)));
	}
	
	@SuppressWarnings("unchecked")
	public void removeUnit(UnitDefinition ud)
	{
		ArrayList<UnitDefinition> units = ((ListConfiguration<UnitDefinition>)super.getPropertyForName("units").getValue()).getFullList();		
		ArrayList<Boolean> enabledList = ((ListConfiguration<UnitDefinition>)super.getPropertyForName("units").getValue()).getEnabledFlags();		
		ArrayList<Usage> usages = ((ListConfiguration<UnitDefinition>)super.getPropertyForName("units").getValue()).getUsages();
		ArrayList<String> names = ((ListConfiguration<UnitDefinition>)super.getPropertyForName("units").getValue()).getNames();
		
		for(int i = 0; i < units.size(); i++)
		{
			if(units.get(i) == ud)
			{
				units.remove(i);
				enabledList.remove(i);
				usages.remove(i);
				names.remove(i);
				break;
			}
		}
		super.setPropertyForName("units", new Property(Usage.CONFIGURATION, new ListConfiguration<UnitDefinition>(units, names, usages, enabledList)));
	}
	
	public void setTechGraphs(List<TechGraph> techGraphs, List<Boolean> enabledList)
	{
		ArrayList<Usage> usages = new ArrayList<Usage>();
		ArrayList<String> names = new ArrayList<String>();
		for(int i = 0; i < techGraphs.size(); i++)
		{
			usages.add(Usage.IMMUTABLE);
			String name = techGraphs.get(i).getName();
			while(names.contains(name))
				name += "_";
			names.add(name); 
		}
		super.setPropertyForName("techs", new Property(Usage.CONFIGURATION, 
				new ListConfiguration<TechGraph>(techGraphs, names, usages, enabledList)));
	}
	
	public List<TechGraph> getAllTechGraphs()
	{
		return ((ListConfiguration<TechGraph>)super.getPropertyForName("techs").getValue()).getFullList();
	}
	
	public List<TechGraph> getUnlockedTechGraphs()
	{
		return ((ListConfiguration<TechGraph>)super.getPropertyForName("techs").getValue()).getEnabledSubList();
	}
		
	@SuppressWarnings("unchecked")
	public void addBuilding(BuildingDefinition bc, boolean enabled)
	{
		ArrayList<BuildingDefinition> buildings = ((ListConfiguration<BuildingDefinition>)super.getPropertyForName("buildings").getValue()).getFullList();		
		ArrayList<Boolean> enabledList = ((ListConfiguration<BuildingDefinition>)super.getPropertyForName("buildings").getValue()).getEnabledFlags();		
		ArrayList<Usage> usages = ((ListConfiguration<BuildingDefinition>)super.getPropertyForName("buildings").getValue()).getUsages();
		ArrayList<String> names = ((ListConfiguration<BuildingDefinition>)super.getPropertyForName("buildings").getValue()).getNames();
		
		buildings.add(bc);
		enabledList.add(enabled);
		usages.add(Usage.CONFIGURATION);
		String name = (String) bc.getPropertyForName("bfgName").getValue();
		while(names.contains(name))
			name += "_";
		names.add(name);
		
		super.setPropertyForName("buildings", new Property(Usage.CONFIGURATION, new ListConfiguration<BuildingDefinition>(buildings, names, usages, enabledList)));
	}
	
	public void removeAllBuildings()
	{
		super.setPropertyForName("buildings", new Property(Usage.CONFIGURATION, 
				new ListConfiguration<BuildingDefinition>(new ArrayList<BuildingDefinition>(), 
						new ArrayList<String>(), new ArrayList<Usage>())));
	}
	
	public void removeAllUnits()
	{
		super.setPropertyForName("units", new Property(Usage.CONFIGURATION, 
				new ListConfiguration<UnitDefinition>(new ArrayList<UnitDefinition>(), 
						new ArrayList<String>(), new ArrayList<Usage>())));
	}
	
	@SuppressWarnings("unchecked")
	public void removeBuilding(BuildingDefinition bc)
	{
		ArrayList<BuildingDefinition> buildings = ((ListConfiguration<BuildingDefinition>)super.getPropertyForName("buildings").getValue()).getFullList();		
		ArrayList<Boolean> enabledList = ((ListConfiguration<BuildingDefinition>)super.getPropertyForName("buildings").getValue()).getEnabledFlags();		
		ArrayList<Usage> usages = ((ListConfiguration<BuildingDefinition>)super.getPropertyForName("buildings").getValue()).getUsages();
		ArrayList<String> names = ((ListConfiguration<BuildingDefinition>)super.getPropertyForName("buildings").getValue()).getNames();
		
		for(int i = 0; i < buildings.size(); i++)
		{
			if(buildings.get(i) == bc)
			{
				buildings.remove(i);
				enabledList.remove(i);
				usages.remove(i);
				names.remove(i);
				break;
			}
		}
		super.setPropertyForName("buildings", new Property(Usage.CONFIGURATION, new ListConfiguration<BuildingDefinition>(buildings, names, usages, enabledList)));
	}
	
	public void setCommandCenter(BuildingDefinition cc)
	{
		super.setPropertyForName("commandCenter", new Property(Usage.CONFIGURATION, cc));
	}
	
	public void setGate(GateDefinition gd)
	{
		super.setPropertyForName("gate", new Property(Usage.CONFIGURATION, gd));
	}
	
	public List<BuildingDefinition> getUnlockedBuildings()
	{
		return ((ListConfiguration<BuildingDefinition>)super.getPropertyForName("buildings").getValue()).getEnabledSubList();
	}
	
	public List<BuildingDefinition> getAllBuildings()
	{
		return ((ListConfiguration<BuildingDefinition>)super.getPropertyForName("buildings").getValue()).getFullList();
	}
	
	public List<UnitDefinition> getUnlockedUnits()
	{
		return ((ListConfiguration<UnitDefinition>)super.getPropertyForName("units").getValue()).getEnabledSubList();
	}
	
	public List<UnitDefinition> getAllUnits()
	{
		return ((ListConfiguration<UnitDefinition>)super.getPropertyForName("units").getValue()).getFullList();
	}
	
	public BuildingDefinition getCommandCenter()
	{
		return (BuildingDefinition) super.getPropertyForName("commandCenter").getValue();
	}
	
	public List<PlayerAbility> getAllPlayerAbilites()
	{
		//for backwards compatability
		if(super.getPropertyForName("playerAbilities") == null || 
				super.getPropertyForName("playerAbilities").getValue() == null)
			return new ArrayList<PlayerAbility>();
		return ((ListConfiguration<PlayerAbility>)super.getPropertyForName("playerAbilities").getValue()).getFullList();
	}
	
	public List<PlayerAbility> getUnlockedPlayerAbilites()
	{
		//for backwards compatability
		if(super.getPropertyForName("playerAbilities") == null || 
				super.getPropertyForName("playerAbilities").getValue() == null)
			return new ArrayList<PlayerAbility>();
		return ((ListConfiguration<PlayerAbility>)super.getPropertyForName("playerAbilities").getValue()).getEnabledSubList();
	}
	
	public void setPlayerAbilities(List<PlayerAbility> abilities, List<Boolean> enabledFlags)
	{
		List<Usage> usages = new ArrayList<Usage>();
		List<String> names = new ArrayList<String>();
		for(PlayerAbility pa : abilities)
		{
			usages.add(Usage.CONFIGURATION);
			String name = pa.getPropertyForName("bfgName").getValue().toString();
			while(names.contains(name))
				name += "_";
			names.add(name);
		}
		
		super.setPropertyForName("playerAbilities", new Property(Usage.CONFIGURATION, 
				new ListConfiguration<PlayerAbility>(abilities, names, usages, enabledFlags)));
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
	
	public GateDefinition getGate()
	{
		return (GateDefinition) super.getPropertyForName("gate").getValue();
	}
	
	public String toString() {
		return getPropertyForName("bfgName").getValue().toString();
	}

}
