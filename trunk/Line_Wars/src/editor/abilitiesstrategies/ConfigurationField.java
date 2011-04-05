package editor.abilitiesstrategies;

import java.util.List;
import java.util.Scanner;

import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.GenericSelector;
import editor.URISelector;
import editor.URISelector.SelectorOptions;
import editor.GenericSelector.*;

public class ConfigurationField extends Field  {

	private BigFrameworkGuy bfg;
	private ConfigType type;
	private GenericSelector<Configuration> selector;
	
	public ConfigurationField(String name, String description, 
			EditorUsage usage, BigFrameworkGuy bfg) {
		this(name, description, usage, bfg, null);
	}
	
	public ConfigurationField(String name, String description, 
			EditorUsage usage, BigFrameworkGuy bfg, Configuration initialConfig) {
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
			case PartConfig:
				type = ConfigType.part;
				break;
			default:
				throw new IllegalArgumentException(usage.toString() + " is not supported by ConfigurationField");
		}
		selector = new GenericSelector<Configuration>("Select " + type.toString() 
				+ " configuration", new SelectConfigurations<Configuration>(bfg, type),
				new ShowBFGName<Configuration>());
		selector.setSelectedObject(initialConfig);
		this.add(selector);
	}

	@Override
	public Object getValue() {
		return selector.getSelectedObject();
	}

}
