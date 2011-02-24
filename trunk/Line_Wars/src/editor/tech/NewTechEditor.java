package editor.tech;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import linewars.gamestate.tech.TechConfiguration;

import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.ConfigurationEditor;
import editor.BigFrameworkGuy.ConfigType;
import editor.tech.modifierEditors.RaceChooser;

/**
 * Provides an interface for the user to edit TechConfiguration objects.
 * 
 * @author Taylor Bergquist
 *
 */
public class NewTechEditor implements ConfigurationEditor {

	private JPanel editorPanel;
	private NTCIEditor baggageEditor;
	private RaceChooser bigCookie;
	
	public NewTechEditor(BigFrameworkGuy bfg){
		editorPanel = new JPanel();
		editorPanel.setLayout(new BorderLayout());
		
		baggageEditor = new NTCIEditor();
		editorPanel.add(baggageEditor.getPanel(), BorderLayout.PAGE_START);
		
		bigCookie = new RaceChooser(bfg);
		editorPanel.add(bigCookie.getPanel(), BorderLayout.CENTER);
	}
	
	@Override
	public void setData(Configuration cd) {
		if(!(cd instanceof TechConfiguration)){
			throw new IllegalArgumentException("The provided Configuration object is not a TechConfiguration object.");
		}
		TechConfiguration toCopy = (TechConfiguration) cd;
		
		baggageEditor.setData(toCopy);
		bigCookie.setData(toCopy);
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		baggageEditor.instantiateNewConfiguration();
		bigCookie.instantiateNewConfiguration();
		return new TechConfiguration();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		if(!(toSet instanceof TechConfiguration)){
			throw new IllegalArgumentException("The provided Configuration object is not a TechConfiguration object.");
		}
		TechConfiguration target = (TechConfiguration) toSet;
		baggageEditor.getData(target);
		bigCookie.getData(target);
		
		return ConfigType.tech;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		ArrayList<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.tech);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return editorPanel;
	}

}
