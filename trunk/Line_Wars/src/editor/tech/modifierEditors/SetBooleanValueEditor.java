package editor.tech.modifierEditors;

import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import linewars.gamestate.tech.ModifierConfiguration;
import configuration.Configuration;
import configuration.Property;
import editor.BigFrameworkGuy.ConfigType;

public class SetBooleanValueEditor extends ModifierEditor {
	
	static{
		ModifierEditor.setEditorForModifier(SetBooleanValue.class, SetBooleanValueEditor.class);
	}
	
	private JPanel panel;
	private ButtonGroup options;
	private JRadioButton trueButton;
	private JRadioButton falseButton;
	
	public SetBooleanValueEditor(Property toDiscard){
		panel = new JPanel();
		
		options = new ButtonGroup();
		trueButton = new JRadioButton("Set the value to true.");
		falseButton = new JRadioButton("Set the value to false.");
		
		options.add(trueButton);
		options.add(falseButton);
		panel.add(trueButton);
		panel.add(falseButton);		
	}
	
	@Override
	public void setData(Configuration cd) {
		if(!(cd instanceof SetBooleanValue)){
			throw new IllegalArgumentException();
		}
		SetBooleanValue source = (SetBooleanValue) cd;
		
		try{
			source.getTargetValue();
		}catch(NullPointerException e){
			trueButton.setSelected(false);
			falseButton.setSelected(false);
			return;
		}
		
		if(source.getTargetValue()){
			trueButton.setSelected(true);
			falseButton.setSelected(false);
		}else{
			falseButton.setSelected(true);
			trueButton.setSelected(false);
		}
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		return new SetBooleanValue();
	}

	@Override
	public void resetEditor() {
		trueButton.setSelected(false);
		falseButton.setSelected(false);
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		if(!(toSet instanceof SetBooleanValue)){
			throw new IllegalArgumentException();
		}
		SetBooleanValue target = (SetBooleanValue) toSet;
		target.setTargetValue(trueButton.isSelected());
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
		SetBooleanValue ret = new SetBooleanValue();
		ret.setTargetValue(trueButton.isSelected());
		return ret;
	}

}
