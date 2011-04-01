package editor.abilitiesstrategies;

import java.util.ArrayList;
import java.util.List;

import configuration.Configuration;
import configuration.ListConfiguration;
import configuration.Usage;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.GenericSelector.GenericListCallback;
import editor.GenericSelector.SelectConfigurations;
import editor.GenericSelector.ShowBFGName;
import editor.ListGenericSelector;

public class ListConfigurationField extends Field {
	
	private BigFrameworkGuy bfg;
	private ConfigType type;
	private ListGenericSelector<Configuration> fullList;
	private ListGenericSelector<Configuration> enabledList;

	public ListConfigurationField(String name, String description,
			EditorUsage usage, BigFrameworkGuy bfg, ListConfiguration<? extends Configuration> initialConfig) {
		super(name, description);
		this.bfg = bfg;
		switch (usage)
		{
			case ListBuildingConfig:
				type = ConfigType.building;
				break;
			case ListUnitConfig:
				type = ConfigType.unit;
				break;
			case ListProjectileConfig:
				type = ConfigType.projectile;
				break;
			case ListTechConfig:
				type = ConfigType.tech;
				break;
		}
		
		fullList = new ListGenericSelector<Configuration>("All configurations", 
				new SelectConfigurations<Configuration>(bfg, type), 
				new ShowBFGName<Configuration>());
		GenericListCallback<Configuration> callback = new GenericListCallback<Configuration>() {

			@Override
			public List<Configuration> getSelectionList() {
				return fullList.getSelectedObjects();
			}
		};
		enabledList = new ListGenericSelector<Configuration>("Initially enabled configurations",
				callback, new ShowBFGName<Configuration>());
		
		if(initialConfig != null)
		{
			fullList.setSelectedObjects(new ArrayList<Configuration>(initialConfig.getFullList()));
			enabledList.setSelectedObjects(new ArrayList<Configuration>(initialConfig.getEnabledSubList()));
		}
		
		this.add(fullList);
		this.add(enabledList);
		
	}

	@Override
	public Object getValue() {
		List<Boolean> flags = new ArrayList<Boolean>();
		List<Usage> usages = new ArrayList<Usage>();
		List<String> names = new ArrayList<String>();
		List<Configuration> data = new ArrayList<Configuration>();
		for(Configuration c : fullList.getSelectedObjects())
		{
			boolean enabled = false;
			for(Configuration ec : enabledList.getSelectedObjects())
				enabled |= (ec == c);
			flags.add(enabled);
			usages.add(Usage.CONFIGURATION);
			String name = c.getPropertyForName("bfgName").getValue().toString();
			while(names.contains(name))
				name += "_";
			names.add(name);
			data.add(c);
		}
		return new ListConfiguration<Configuration>(data, names, usages, flags);
	}

}
