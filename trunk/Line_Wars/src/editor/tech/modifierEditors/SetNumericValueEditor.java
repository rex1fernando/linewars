package editor.tech.modifierEditors;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

import linewars.gamestate.tech.ModifierConfiguration;
import configuration.Configuration;
import configuration.Property;
import configuration.Usage;
import editor.BigFrameworkGuy.ConfigType;

public class SetNumericValueEditor extends ModifierEditor {
	
	static{
		ModifierEditor.setEditorForModifier(SetNumericValueModification.class, SetNumericValueEditor.class);
	}
	
	private JPanel panel;
	private JTextField textBox;
	private Usage valueType;
	
	public SetNumericValueEditor(Property toTypeCheck){
		valueType = toTypeCheck.getUsage();
		
		panel = new JPanel();
		textBox = new JTextField(20);
		
		panel.add(textBox);
		if(toTypeCheck != null && toTypeCheck.getValue() != null){
			textBox.setText(toTypeCheck.getValue().toString());
		}
	}

	@Override
	public void setData(Configuration cd) {
		SetNumericValueModification source = (SetNumericValueModification) cd;
		try{
			source.getTargetValue();
		}catch(NullPointerException e){
			textBox.setText("");
			return;
		}
		
		textBox.setText("" + source.getTargetValue());
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		return new SetNumericValueModification();
	}

	@Override
	public void resetEditor() {
		textBox.setText("");
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		SetNumericValueModification target = (SetNumericValueModification) toSet;
		
		Double targetValue;
		try{
			targetValue = Double.parseDouble(textBox.getText());
		}catch(Exception e){
			targetValue = 0.0;
		}
		
		if(valueType == Usage.NUMERIC_INTEGER){
			targetValue = new Double(targetValue.intValue());
		}
		
		target.setTargetValue(targetValue);
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
		SetNumericValueModification ret = (SetNumericValueModification) instantiateNewConfiguration();
		getData(ret);
		return ret;
	}

}
