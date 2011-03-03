package editor.tech;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSeparator;

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
public class TechEditor implements ConfigurationEditor {

	private JPanel editorPanel;
	private NCTIEditor baggageEditor;
	private RaceChooser bigCookie;
	
	public TechEditor(BigFrameworkGuy bfg){
		editorPanel = new JPanel();
		editorPanel.setLayout(new BorderLayout());
		editorPanel.setPreferredSize(new Dimension(700, 600));
		
		baggageEditor = new NCTIEditor();
		baggageEditor.instantiateNewConfiguration();
		JPanel forASeparator = new JPanel();
		forASeparator.setLayout(new BorderLayout());
		forASeparator.add(baggageEditor.getPanel(), BorderLayout.CENTER);
		forASeparator.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.PAGE_END);
		editorPanel.add(forASeparator, BorderLayout.PAGE_START);
		
		bigCookie = new RaceChooser(bfg);
		bigCookie.instantiateNewConfiguration();
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
	
	public void resetEditor()
	{
		baggageEditor.resetEditor();
		bigCookie.resetEditor();
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
