package editor.mapitems;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import linewars.gamestate.mapItems.PartDefinition;

import configuration.Configuration;
import editor.ConfigurationEditor;
import editor.BigFrameworkGuy.ConfigType;

public class PartEditor extends JPanel implements ConfigurationEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1698135020265565651L;

	@Override
	public void setData(Configuration cd) {}
	
	public void resetEditor() {}

	@Override
	public Configuration instantiateNewConfiguration() {
		return new PartDefinition();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		return ConfigType.part;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.part);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

}
