package editor.tech.modifierEditors;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import linewars.gamestate.tech.ModifierConfiguration;
import configuration.Configuration;
import configuration.Property;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.BigFrameworkGuy.ConfigurationWrapper;

public class SetConfigurationEditor extends ModifierEditor {
	
	static{
		ModifierEditor.setEditorForModifier(SetConfigurationModification.class, SetConfigurationEditor.class);
	}
	
	private JPanel panel;
	private JList selector;
	private BigFrameworkGuy bfgReference;
	
	public SetConfigurationEditor(Property toIgnore, BigFrameworkGuy bfgReference){
		panel = new JPanel();
		this.bfgReference = bfgReference;
		
		ConfigType[] allTypes = ConfigType.values();
		List<ConfigType> alsoAllTypes = new ArrayList<ConfigType>();
		for(ConfigType toAdd : allTypes){
			alsoAllTypes.add(toAdd);
		}
		
		selector = bfgReference.createMulitSelectionList(alsoAllTypes, false);
		JScrollPane scroller = new JScrollPane(selector);
		scroller.setPreferredSize(new Dimension(350, 450));
		
		panel.add(scroller);
	}

	@Override
	public void setData(Configuration cd) {
		SetConfigurationModification source = (SetConfigurationModification) cd;
		
		Configuration selected = null;
		try{
			selected = source.getReplacement();
		}catch(NullPointerException e){}
		
		if(selected != null){
			selector.setSelectedValue(bfgReference.new ConfigurationWrapper(selected, ConfigType.tech), true);
		}else{
			selector.clearSelection();
		}
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		return new SetConfigurationModification();
	}

	@Override
	public void resetEditor() {
		selector.clearSelection();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		SetConfigurationModification target = (SetConfigurationModification) toSet;
		
		if(selector.isSelectionEmpty()){
			target.setReplacement(null);			
		}else{
			target.setReplacement((Configuration) ((ConfigurationWrapper) selector.getSelectedValue()).getConfiguration());
		}
		return null;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		return null;
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public ModifierConfiguration getData() {
		SetConfigurationModification ret = (SetConfigurationModification) instantiateNewConfiguration();
		getData(ret);
		return ret;
	}

}
