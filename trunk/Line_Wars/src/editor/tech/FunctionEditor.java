package editor.tech;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;

import editor.BigFrameworkGuy;
import editor.ConfigurationEditor;
import editor.URISelector;
import editor.BigFrameworkGuy.NewButtonListener;
import editor.URISelector.SelectorOptions;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

public class FunctionEditor extends JPanel implements ConfigurationEditor, SelectorOptions, ActionListener {
	
	private ArrayList<CoefficientFrame> frames = new ArrayList<FunctionEditor.CoefficientFrame>();
	private URISelector type;
	private JButton addFrames;
	
	public FunctionEditor()
	{
		addFrames = new JButton("Add Coefficient");
		addFrames.addActionListener(this);
		
		type = new URISelector("Function Type", this);
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(type);
	}

	@Override
	public void setData(ConfigData cd) {
		setData(cd, false);
	}

	@Override
	public void forceSetData(ConfigData cd) {
		setData(cd, true);
	}
	
	private void setData(ConfigData cd, boolean force)
	{
		this.reset();
		if (cd.getDefinedKeys().contains(ParserKeys.functionType)
				&& cd.getString(ParserKeys.functionType) != null
				&& cd.getDefinedKeys().contains(ParserKeys.coefficients)
				&& cd.getNumberList(ParserKeys.coefficients).size() > 0)
		{
			type.setSelectedURI(cd.getString(ParserKeys.functionType));
			if(cd.getString(ParserKeys.functionType).equalsIgnoreCase("exponential"))
			{
				List<Double> cos = cd.getNumberList(ParserKeys.coefficients);
				if(cos.size() == 3 || force)
				{
					for(int i = 0; i < cos.size(); i++)
					{
						this.addCoefficient("Exponential");
						this.frames.get(this.frames.size() - 1).setDouble(cos.get(i));
					}
				}
				else
					throw new IllegalArgumentException("Exactly 3 coefficients must be defined for exponential");
				addFrames.setEnabled(false);
			}
			else if(cd.getString(ParserKeys.functionType).equalsIgnoreCase("polynomial"))
			{
				List<Double> cos = cd.getNumberList(ParserKeys.coefficients);
				
				for(int i = 0; i < cos.size(); i++)
				{
					this.addCoefficient("Polynomial");
					this.frames.get(this.frames.size() - 1).setDouble(cos.get(i));
				}
				addFrames.setEnabled(true);
			}
			else if(force)
				this.reset();
			else
				throw new IllegalArgumentException("Function type not defined");
		}
		else if(force)
			this.reset();
		else
			throw new IllegalArgumentException("Function type not defined or keys not defined");
		
		this.validate();
		this.updateUI();
	}

	@Override
	public void reset() {
		type.setSelectedURI("");
		
		for(CoefficientFrame f : frames)
			this.remove(f);
		frames.clear();
		
		this.remove(addFrames);
		
		this.validate();
		this.updateUI();
	}

	@Override
	public ConfigData getData() {
		ConfigData cd = new ConfigData();
		cd.set(ParserKeys.functionType, type.getSelectedURI());
		for(CoefficientFrame f : frames)
			cd.add(ParserKeys.coefficients, f.getDouble());
		return cd;
	}

	@Override
	public boolean isValidConfig() {
		for(CoefficientFrame f : frames)
			if(!f.isDouble())
				return false;
		if(frames.size() != 3 && type.getSelectedURI().equalsIgnoreCase("exponential"))
			return false;
		return true;
	}

	@Override
	public ParserKeys getType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

	
	private class CoefficientFrame extends JPanel  {
		
		private JTextField value;
		
		public CoefficientFrame(int degree, String fType) {
			value = new JTextField();
			value.setColumns(10);
			
			if(fType.equalsIgnoreCase("Polynomial")){
				String dString = String.valueOf(degree);
				if(degree == 0){
					this.add(new JLabel("(x ^ "+dString + ") * "));
				}else{
					this.add(new JLabel("+ (x ^ "+dString + ") * "));
				}
			}else if(fType.equalsIgnoreCase("Exponential")){
				if(degree == 0){
					this.add(new JLabel("Const:"));
				}else if(degree == 1){
					this.add(new JLabel("* :"));
				}else if(degree == 2){
					this.add(new JLabel("^ :"));
				}
			}

			this.add(value);
		}
		
		public boolean isDouble()
		{
			return new Scanner(value.getText()).hasNextDouble();
		}
		
		public double getDouble()
		{
			return new Scanner(value.getText()).nextDouble();
		}
		
		public void setDouble(double d) {
			value.setText(d + "");
		}
	}

	@Override
	public String[] getOptions() {
		return new String[]{"Polynomial", "Exponential"};
	}

	@Override
	public void uriSelected(String uri) {
		this.reset();
		if(uri.equalsIgnoreCase("Exponential"))
		{
			this.addCoefficient("Exponential");
			this.addCoefficient("Exponential");
			this.addCoefficient("Exponential");
			this.addFrames.setEnabled(false);
		}
		else if(uri.equalsIgnoreCase("Polynomial"))
		{
			this.addCoefficient("Polynomial");
			this.addFrames.setEnabled(true);
		}
		type.setSelectedURI(uri);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(type.getSelectedURI().equalsIgnoreCase("Exponential")){
			this.addCoefficient("Exponential");		
		}else if(type.getSelectedURI().equalsIgnoreCase("Polynomial")){
			this.addCoefficient("Polynomial");
		}
	}
	
	private void addCoefficient(String fType)
	{
		this.remove(addFrames);
		
		this.frames.add(new CoefficientFrame(this.frames.size(), fType));
		this.add(this.frames.get(this.frames.size() - 1));
		
		this.add(addFrames);
		
		this.validate();
		this.updateUI();
	}

	

}
