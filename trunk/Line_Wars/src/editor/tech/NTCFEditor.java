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
import javax.swing.JTextArea;
import javax.swing.JTextField;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Function;
import editor.ConfigurationEditor;

public class NTCFEditor implements ConfigurationEditor, ActionListener, FocusListener {

	private JPanel panel;
	private ConfigData NTCFData;
	private String name = null;
	private String tooltip = null;
	private Function costFunction = null;
	
	private FunctionPanel fPanel;
	private JTextField nameField;
	private JTextArea tooltipArea;
	private JButton functionEditorButton;
	
	public NTCFEditor(){
		NTCFData = new ConfigData();
		panel = new JPanel();
		JLabel nameLabel = new JLabel("Name: ");
		JLabel tooltipLabel = new JLabel("Tooltip: ");
		nameField = new JTextField(20);
		tooltipArea = new JTextArea();
		JScrollPane scroller = new JScrollPane(tooltipArea);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		functionEditorButton = new JButton("Edit Cost Function");
		//TODO Make this scrollbar work.

		nameField.addActionListener(this);
		tooltipArea.addFocusListener(this);
		functionEditorButton.addActionListener(this);
		tooltipArea.setLineWrap(true);
		tooltipArea.setWrapStyleWord(true);
		tooltipArea.setPreferredSize(new Dimension(200, 200));
		scroller.setPreferredSize(new Dimension(200, 100));
		
		panel.add(nameLabel);
		panel.add(nameField);
		panel.add(tooltipLabel);
		panel.add(scroller);
		panel.add(functionEditorButton);
	}
	
	@Override
	public void setData(ConfigData cd) {
		if(isValid(cd)){
			forceSetData(cd);
		}else{
			throw new IllegalArgumentException("ConfigData " +cd +" is invalid.");
		}
	}

	@Override
	public void forceSetData(ConfigData cd) {
		
		NTCFData = new ConfigData();
		if(cd.getDefinedKeys().contains(ParserKeys.name) && cd.getString(ParserKeys.name) != null ){
			NTCFData.add(ParserKeys.name, cd.getString(ParserKeys.name));
		}
		
		if(cd.getDefinedKeys().contains(ParserKeys.tooltip) && cd.getString(ParserKeys.tooltip) != null){
			NTCFData.add(ParserKeys.tooltip, cd.getString(ParserKeys.tooltip));
		}
		
		if(cd.getDefinedKeys().contains(ParserKeys.costFunction) && cd.getConfig(ParserKeys.costFunction) != null){
			NTCFData.add(ParserKeys.costFunction, cd.getString(ParserKeys.costFunction));
		}

	}

	@Override
	public void reset() {
		NTCFData = new ConfigData();
		name = null;
		tooltip = null;
		costFunction = null;
		nameField.setText(null);
		tooltipArea.setText(null);
	}

	@Override
	public ConfigData getData() {
		NTCFData.add(ParserKeys.name, name);
		NTCFData.add(ParserKeys.tooltip, tooltip);
		if(costFunction != null){
			NTCFData.add(ParserKeys.costFunction, costFunction.toConfigData());			
		}
		return NTCFData;
	}

	@Override
	public boolean isValidConfig() {
		return isValid(NTCFData);
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
			//TODO make sure the function is valid.
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
	
	public static void main(String[] args) {
		NTCFEditor ne = new NTCFEditor();
		JFrame frame = new JFrame("test frame");
		frame.setContentPane(ne.getPanel());
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() ==  functionEditorButton){
			//TODO Set up us the function editor.
			System.out.println("The button was pushed.");
		}else if(arg0.getSource() == nameField){
			name = nameField.getText();
			System.out.println(name);
		}else{
			System.out.print("WTF? This came from " +arg0.getSource() +"?");
		}
	}

	/**
	 * Goggles.
	 */
	@Override
	public void focusGained(FocusEvent arg0) {}

	@Override
	public void focusLost(FocusEvent arg0) {
		if(arg0.getSource() == tooltipArea){
			if(!tooltipArea.getText().equals("")){
				tooltip = tooltipArea.getText();
			}else{
				System.out.println("Didnt set the string.");
			}
		System.out.println(tooltip);
		}else{
			System.out.print("WTF? This came from " +arg0.getSource() +"?");
		}
		
	}

}
