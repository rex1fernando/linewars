package editor;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import editor.abilities.AbilityEditor;
import editor.animations.AnimationEditor;
import editor.mapEditor.MapEditor;
import editor.mapitems.MapItemEditor;
import editor.tech.TechEditor;


import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileReader;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.configfilehandler.ParserKeys;

public class BigFrameworkGuy
{
	private ConfigData masterList;
	
	private JFrame frame;
	private JTabbedPane tabPanel;
	
	private List<ConfigurationEditor> editors;
	
	public BigFrameworkGuy() throws FileNotFoundException, InvalidConfigFileException
	{
		masterList = new ConfigFileReader("resources/masterList.cfg").read();
		
		tabPanel = new JTabbedPane();
		
		editors = new ArrayList<ConfigurationEditor>();
		
		frame = new JFrame();
		
		//add each editor
		
		String imagesFolder = new File(this.getAnimationURIs()[0]).getParentFile().getAbsolutePath();
		AnimationEditor toStart = new AnimationEditor(imagesFolder); 
		
		Dimension prefferedSize = new Dimension(0, 0);
		String[] editors = {"Map Item", "Ability", "Animation", "Map", "Tech"};
		for(String e : editors)
		{
			ConfigurationEditor ce = null;
			if(e.equals("Map Item"))
				ce = new MapItemEditor(this);
			else if(e.equals("Ability"))
				ce = new AbilityEditor(this);
			else if(e.equals("Animation"))
				ce = toStart;
			else if(e.equals("Map"))
				ce = new MapEditor(frame);
			else if(e.equals("Tech"))
				ce = new TechEditor(this);
			
			tabPanel.addTab(e + " Editor", ce.getPanel());
			this.editors.add(ce);
			
			if(ce.getPanel().getPreferredSize().getWidth() > prefferedSize.getWidth())
				prefferedSize.setSize(ce.getPanel().getPreferredSize().getWidth(), prefferedSize.getHeight());
			if(ce.getPanel().getPreferredSize().getHeight() > prefferedSize.getHeight())
				prefferedSize.setSize(prefferedSize.getWidth(), ce.getPanel().getPreferredSize().getHeight());
		}
		
		//set up the frame
		frame.add(tabPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(prefferedSize);
		frame.setVisible(true);
		
		//can't start the animation editors display loop until the panel is added to the frame
		new Thread(toStart).run();
		
	}
	
	public String[] getCommandCenterURIs()
	{
		return masterList.getStringList(ParserKeys.buildingURI).toArray(new String[0]);
	}
	
	public String[] getUnitURIs()
	{
		return masterList.getStringList(ParserKeys.unitURI).toArray(new String[0]);
	}
	
	public String[] getBuildingURIs()
	{
		return masterList.getStringList(ParserKeys.buildingURI).toArray(new String[0]);
	}
	
	public String[] getTechURIs()
	{
		return masterList.getStringList(ParserKeys.techURI).toArray(new String[0]);
	}
	
	public String[] getGateURIs()
	{
		return masterList.getStringList(ParserKeys.gateURI).toArray(new String[0]);
	}

	public String[] getProjectileURIs() {
		return masterList.getStringList(ParserKeys.projectileURI).toArray(new String[0]);
	}

	public String[] getAnimationURIs() {
		return masterList.getStringList(ParserKeys.animationURI).toArray(new String[0]);
	}

	public String[] getAbilityURIs() {
		return masterList.getStringList(ParserKeys.abilityURI).toArray(new String[0]);
	}
	
	public static void main(String[] args) throws FileNotFoundException, InvalidConfigFileException {
		new BigFrameworkGuy();
	}
}
