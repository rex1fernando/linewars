package editor.mapitems;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import linewars.gamestate.mapItems.TurretDefinition;
import linewars.gamestate.mapItems.strategies.turret.TurretStrategyConfiguration;

import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.ConfigurationEditor;
import editor.BigFrameworkGuy.ConfigType;
import editor.GenericSelector;

public class TurretEditor extends JPanel implements ConfigurationEditor {
	
	private GenericSelector<Configuration> turretStrat;
	
	public TurretEditor(BigFrameworkGuy bfg)
	{
		turretStrat = new GenericSelector<Configuration>("Turret Strategy", 
					new GenericSelector.SelectConfigurations<Configuration>(bfg, ConfigType.turretStrategy));
		this.add(turretStrat);
	}

	@Override
	public void setData(Configuration cd) {
		TurretDefinition td = (TurretDefinition)cd;
		turretStrat.setSelectedObject(td.getTurretStratConfig());
	}
	
	public void resetEditor()
	{
		turretStrat.setSelectedObject(null);
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		return new TurretDefinition();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		TurretDefinition td = (TurretDefinition)toSet;
		td.setTurretStratConfig((TurretStrategyConfiguration) turretStrat.getSelectedObject());
		return ConfigType.turret;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.turret);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

}
