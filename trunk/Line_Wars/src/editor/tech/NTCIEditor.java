package editor.tech;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;


import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import configuration.Configuration;

import linewars.display.IconConfiguration;
import linewars.display.IconConfiguration.IconType;
import linewars.gamestate.tech.TechConfiguration;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.IconEditor;

/**
 * 
 * @author John George, Taylor Bergquist
 *
 */
public class NTCIEditor implements ConfigurationEditor {
	
	private static final List<IconType> iconTypes;
	private static final List<String> iconDescriptions;

	static{
		iconTypes = new ArrayList<IconType>();
		iconTypes.add(IconType.regular);
		iconTypes.add(IconType.rollover);
		iconTypes.add(IconType.highlighted);
		iconTypes.add(IconType.pressed);
		iconTypes.add(IconType.disabled);
		
		iconDescriptions = new ArrayList<String>();
		iconDescriptions.add("Displayed when the tech is researchable and not being interacted with at the moment.");
		iconDescriptions.add("Displayed when the user moves the mouse over a researchable tech.");
		iconDescriptions.add("Displayed when the user selects a Tech with the Tab key or some similar mechanism.");
		iconDescriptions.add("Displayed when the user clicks on the Tech (while the mouse is held down).");
		iconDescriptions.add("Displayed when the Tech is locked and cannot be researched.");
	}

	private JPanel panel;
	
	private JTextField nameField;
	private JTextArea tooltipArea;
	private JTextField costField;
	
	private IconEditor iconEditor;
	
	public NTCIEditor(){
		iconEditor = new IconEditor(iconTypes, iconDescriptions);
		
		//Initialize the elements
		panel = new JPanel();
		JLabel nameLabel = new JLabel("Name: ");
		JLabel tooltipLabel = new JLabel("Tooltip: ");
		nameField = new JTextField(20);
		tooltipArea = new JTextArea();
		costField = new JTextField(8);
		JScrollPane scroller = new JScrollPane(tooltipArea);
		
		//Set the attributes of the elements
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		tooltipArea.setLineWrap(true);
		tooltipArea.setWrapStyleWord(true);
		tooltipArea.setPreferredSize(new Dimension(200, 200));
		scroller.setPreferredSize(new Dimension(200, 100));
		
		//Add the elements to the main panel
		panel.add(nameLabel);
		panel.add(nameField);
		panel.add(tooltipLabel);
		panel.add(scroller);
		panel.add(costField);
		panel.add(new JSeparator(JSeparator.VERTICAL));
		panel.add(iconEditor.getPanel());
	}
	
	@Override
	public void setData(Configuration cd) {
		instantiateNewConfiguration();
		if(!(cd instanceof TechConfiguration)){
			throw new IllegalArgumentException();
		}
		TechConfiguration source = (TechConfiguration) cd;
		
		nameField.setText(source.getName());
		tooltipArea.setText(source.getTooltip());
		costField.setText("" + source.getCost());
		iconEditor.setData(source.getIcons());
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		nameField.setText(null);
		tooltipArea.setText(null);
		costField.setText(null);
		iconEditor.instantiateNewConfiguration();
		
		return new TechConfiguration();
	}

	//TODO handle exceptions properly
	@Override
	public ConfigType getData(Configuration toSet) {
		// make sure it is the right type, then cast
		if(!(toSet instanceof TechConfiguration)){
			throw new IllegalArgumentException();
		}
		TechConfiguration target = (TechConfiguration) toSet;
		
		// name
		target.setName(nameField.getText());
		
		// tooltip
		target.setTooltip(tooltipArea.getText());
		
		// cost
		target.setCost(Double.valueOf(costField.getText()));
		
		// icons
		IconConfiguration icons = new IconConfiguration();
		
		iconEditor.getData(icons);
		target.setIcons(icons);
		return ConfigType.tech;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.tech);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

}
