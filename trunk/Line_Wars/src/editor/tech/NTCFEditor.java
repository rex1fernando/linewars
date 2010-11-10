package editor.tech;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Function;
import editor.ConfigurationEditor;
import editor.IconEditor;

public class NTCFEditor implements ConfigurationEditor, FocusListener {

	private JPanel panel;
	private String name = null;
	private String tooltip = null;
	
	private JTextField nameField;
	private JTextArea tooltipArea;
	
	private FunctionEditor costFunctionEditor;
	private IconEditor iconEditor;
	
	public NTCFEditor(){
		costFunctionEditor = new FunctionEditor();
		iconEditor = new IconEditor();
		
		panel = new JPanel();
		JLabel nameLabel = new JLabel("Name: ");
		JLabel tooltipLabel = new JLabel("Tooltip: ");
		nameField = new JTextField(20);
		tooltipArea = new JTextArea();
		JScrollPane scroller = new JScrollPane(tooltipArea);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//TODO Make this scrollbar work.

		nameField.addFocusListener(this);
		tooltipArea.addFocusListener(this);
		tooltipArea.setLineWrap(true);
		tooltipArea.setWrapStyleWord(true);
		tooltipArea.setPreferredSize(new Dimension(200, 200));
		scroller.setPreferredSize(new Dimension(200, 100));
		
		panel.add(nameLabel);
		panel.add(nameField);
		panel.add(tooltipLabel);
		panel.add(scroller);
		panel.add(new JSeparator(JSeparator.VERTICAL));
		panel.add(costFunctionEditor.getPanel());
		panel.add(new JSeparator(JSeparator.VERTICAL));
		panel.add(iconEditor.getPanel());
	}
	
	@Override
	public void setData(ConfigData cd) {
		reset();
		if(isValid(cd)){
			forceSetData(cd);
		}else{
			throw new IllegalArgumentException("ConfigData " +cd +" is invalid.");
		}
	}

	@Override
	public void forceSetData(ConfigData cd) {
		if(cd.getDefinedKeys().contains(ParserKeys.name) && cd.getString(ParserKeys.name) != null ){
			name = cd.getString(ParserKeys.name);
			nameField.setText(name);
		}
		
		if(cd.getDefinedKeys().contains(ParserKeys.tooltip) && cd.getString(ParserKeys.tooltip) != null){
			tooltip = cd.getString(ParserKeys.tooltip);
			tooltipArea.setText(tooltip);
		}
		
		if(cd.getDefinedKeys().contains(ParserKeys.costFunction) && cd.getConfig(ParserKeys.costFunction) != null){
			costFunctionEditor.setData(cd.getConfig(ParserKeys.costFunction));
		}
		
		iconEditor.forceSetData(cd);
	}

	@Override
	public void reset() {
		name = null;
		tooltip = null;
		nameField.setText(null);
		tooltipArea.setText(null);
		costFunctionEditor.reset();
		iconEditor.reset();
	}

	@Override
	public ConfigData getData() {
		ConfigData ret = new ConfigData();
		ret.add(ParserKeys.name, name);
		ret.add(ParserKeys.tooltip, tooltip);
		ret.add(ParserKeys.costFunction, costFunctionEditor.getData());
		ConfigData iconData = iconEditor.getData();
		for(ParserKeys toAdd : iconData.getDefinedKeys()){
			ret.set(toAdd, iconData.getStringList(toAdd).toArray(new String[0]));
			ret.set(toAdd, iconData.getConfigList(toAdd).toArray(new ConfigData[0]));
		}
		return ret;
	}

	@Override
	public boolean isValidConfig() {
		return isValid(getData());
	}
	
	private boolean isValid(ConfigData cd)
	{
		if(!cd.getDefinedKeys().contains(ParserKeys.name)){
			return false;
		}
		
		if(!cd.getDefinedKeys().contains(ParserKeys.tooltip)){
			return false;
		}
		
		if(!cd.getDefinedKeys().contains(ParserKeys.costFunction)){
			return false;
		}

		try{
			costFunctionEditor.setData(cd.getConfig(ParserKeys.costFunction));
		}catch(Exception e){
			return false;
		}
		
		try{
			iconEditor.setData(cd);
		}catch(Exception e){
			return false;
		}
		
		return true;
	}

	@Override
	public ParserKeys getType() {
		return ParserKeys.name;
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	/**
	 * Goggles.
	 */
	@Override
	public void focusGained(FocusEvent arg0) {}

	@Override
	public void focusLost(FocusEvent arg0) {
		if(arg0.getSource() == nameField){
			name = nameField.getText();
		}else if(arg0.getSource() == tooltipArea){
			tooltip = tooltipArea.getText();
		}else{
			System.out.print("WTF? This came from " +arg0.getSource() +"?");
		}
	}

}
