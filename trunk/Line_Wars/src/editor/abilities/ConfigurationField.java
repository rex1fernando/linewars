package editor.abilities;

import java.util.List;
import java.util.Scanner;

import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.URISelector;
import editor.URISelector.SelectorOptions;

public class ConfigurationField extends Field implements SelectorOptions {

	private BigFrameworkGuy bfg;
	private ConfigType type;
	private URISelector selector;
	
	private List<Configuration> lastGeneratedConfigs;
	
	public ConfigurationField(String name, String description, 
			EditorUsage usage, BigFrameworkGuy bfg) {
		super(name, description);
		this.bfg = bfg;
		switch (usage)
		{
			case BuildingConfig:
				type = ConfigType.building;
				break;
			case UnitConfig:
				type = ConfigType.unit;
				break;
			case ProjectileConfig:
				type = ConfigType.projectile;
				break;
			case TechConfig:
				type = ConfigType.tech;
				break;
		}
		selector = new URISelector("Select " + type.toString() 
				+ " configuration", this);
		this.add(selector);
	}

	@Override
	public Object getValue() {
		Scanner s = new Scanner(selector.getSelectedURI());
		s.useDelimiter(":");
		int i = s.nextInt();
		return lastGeneratedConfigs.get(i);
	}

	@Override
	public String[] getOptions() {
		lastGeneratedConfigs = bfg.getConfigurationsByType(type);
		String[] ret = new String[lastGeneratedConfigs.size()];
		for(int i = 0; i < lastGeneratedConfigs.size(); i++)
			ret[i] = i + ":" + ((String) lastGeneratedConfigs.get(i)
							.getPropertyForName("bfgName").getValue());
		return ret;
	}

	@Override
	public void uriSelected(String uri) {}

}
