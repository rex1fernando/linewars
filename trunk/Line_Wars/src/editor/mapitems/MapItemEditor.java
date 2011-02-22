package editor.mapitems;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.GateDefinition;
import linewars.gamestate.mapItems.PartDefinition;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.TurretDefinition;
import linewars.gamestate.mapItems.UnitDefinition;

import configuration.Configuration;

import editor.BigFrameworkGuy;
import editor.ConfigurationEditor;
import editor.BigFrameworkGuy.ConfigType;

public class MapItemEditor extends JPanel implements ConfigurationEditor {
	
	private ConfigurationEditor commanalities;
	private ConfigurationEditor variabilities;
	
	private BigFrameworkGuy bfg;
	
	public MapItemEditor(BigFrameworkGuy guy)
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		bfg = guy;
	}

	@Override
	public void setData(Configuration cd) {
		commanalities = new MapItemCommanalitiesEditor(bfg);
		commanalities.setData(cd);
		variabilities = getConfigEditor(getType(cd));
		variabilities.setData(cd);

		this.removeAll();
		this.add(commanalities.getPanel());
		this.add(variabilities.getPanel());
		this.validate();
		this.updateUI();
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		Object[] possibilities = {"Unit", "Building", "Projectile", "Gate", "Turret", "Part"};
		String type = (String) JOptionPane.showInputDialog(this,
				"Which type of map item would you look to create?",
				"Select type", JOptionPane.PLAIN_MESSAGE, null, possibilities,
				"Unit");
		
		commanalities = new MapItemCommanalitiesEditor(bfg);
		variabilities = getConfigEditor(getType(type));
		
		this.removeAll();
		this.add(commanalities.getPanel());
		this.add(variabilities.getPanel());
		this.validate();
		this.updateUI();
		return variabilities.instantiateNewConfiguration();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		commanalities.getData(toSet);
		return variabilities.getData(toSet);
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.unit);
		ret.add(ConfigType.building);
		ret.add(ConfigType.gate);
		ret.add(ConfigType.projectile);
		ret.add(ConfigType.part);
		ret.add(ConfigType.turret);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}
	
	private ConfigType getType(String s)
	{
		return ConfigType.valueOf(s.toLowerCase());
	}
	
	private ConfigType getType(Configuration c)
	{
		if(c instanceof GateDefinition)
			return ConfigType.gate;
		else if(c instanceof UnitDefinition)
			return ConfigType.unit;
		else if(c instanceof BuildingDefinition)
			return ConfigType.building;
		else if(c instanceof ProjectileDefinition)
			return ConfigType.projectile;
		else if(c instanceof TurretDefinition)
			return ConfigType.turret;
		else if(c instanceof PartDefinition)
			return ConfigType.part;
		else
			throw new IllegalArgumentException("Configuration type not identified");
	}
	
	private ConfigurationEditor getConfigEditor(ConfigType type)
	{
		if(type.equals(ConfigType.unit))
			return new UnitEditor(bfg);
		else if(type.equals(ConfigType.building))
			return new BuildingEditor();
		else if(type.equals(ConfigType.projectile))
			return new ProjectileEditor(bfg);
		else if(type.equals(ConfigType.gate))
			return new GateEditor();
		else if(type.equals(ConfigType.turret))
			return new TurretEditor(bfg);
		else if(type.equals(ConfigType.part))
			return new PartEditor();
		else
			throw new IllegalArgumentException(type.toString() + " not recognized");
	}

}
