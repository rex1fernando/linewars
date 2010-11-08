package editor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import editor.mapitems.MapItemEditor;


import linewars.configfilehandler.ConfigData;

public class BigFrameworkGuy
{
	private ConfigData masterList;
	
	private JFrame frame;
	private JTabbedPane tabPanel;
	
	private List<ConfigurationEditor> editors;
	
	public BigFrameworkGuy()
	{
		tabPanel = new JTabbedPane();
		
		editors = new ArrayList<ConfigurationEditor>();
		ConfigurationEditor ce;
		JScrollPane scroller;
		
		//add each editor
		
		//add the map editor
		//TODO
		
		//add the race editor
		//TODO
		
		//add the tech editor
		//TODO
		
		//add the map item editor
		ce = new MapItemEditor(this);
		scroller = new JScrollPane();
		scroller.add(ce.getPanel());
		scroller.setPreferredSize(ce.getPanel().getPreferredSize());
		tabPanel.addTab("Map Item Editor", ce.getPanel());
		
		frame = new JFrame();
		frame.add(tabPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	public String[] getCommandCenterURIs()
	{
		// TODO implement
		return null;
	}
	
	public String[] getUnitURIs()
	{
		// TODO implement
		return null;
	}
	
	public String[] getBuildingURIs()
	{
		// TODO implement
		return null;
	}
	
	public String[] getTechURIs()
	{
		// TODO implement
		return null;
	}
	
	public String[] getGateURIs()
	{
		// TODO implement
		return null;
	}

	public String[] getProjectileURIs() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAnimationURIs() {
		return new String[]{"resources/animations/commandCenterIdle.cfg", "a1", "a2"};
//		return null; TODO
	}

	public String[] getAbilityURIs() {
		return new String[]{"ability1", "ability2", "ability3"};
//		return null; TODO
	}
	
	public static void main(String[] args) {
		new BigFrameworkGuy();
	}
}
