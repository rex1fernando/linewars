package editor.mapitems;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.*;

import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.URISelector;
import editor.URISelector.SelectorOptions;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

/**
 * 
 * @author Connor Schenck
 *
 * This class represents the panel that allows users
 * to select the parameters for a strategy.
 *
 */
public class StrategySelector extends JPanel implements SelectorOptions, ActionListener, ConfigurationEditor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4372052077303827548L;

	public interface StrategySelectorCallback {
		public void setConfigForStrategy(StrategySelector caller, ConfigData cd);
	}
	
	public enum StrategySelectorFieldType {
		numeric, string
	}
	
	//variable for holding the type
	private URISelector type;
	private String[] availableStrategies;
	
	//variables for holding the fields
	private Map<String, Map<ParserKeys, StrategySelectorFieldType>> fieldMap;
	private Map<ParserKeys, JTextField> fields;
	
	//variable for holding the person to call when we're done
	private StrategySelectorCallback callBack;
	
	//the buttons for being done or canceling
	private JButton done;
	private JButton cancel;
	
	//the frame
	private JFrame frame;
	private String title;
	
	/**
	 * Constructs this strategy selector. Takes in a callback (to
	 * call when the user hits 'done'), a title for the frame, and
	 * a map of types of strategies to maps of keys for that strategy
	 * to field types. Creates a frame to display this editor in.
	 * 
	 * @param callback	the class to call when the user hits 'done'.
	 * @param title		the title of the frame
	 * @param fieldMap	the map of types of strategies
	 */
	public StrategySelector(StrategySelectorCallback callback, String title,
			Map<String, Map<ParserKeys, StrategySelectorFieldType>> fieldMap)
	{
		this.title = title;
		this.callBack = callback;
		this.fieldMap = fieldMap;
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//set up the type selector
		List<String> strats = new ArrayList<String>();
		for(Entry<String, Map<ParserKeys, StrategySelectorFieldType>> e : fieldMap.entrySet())
			strats.add(e.getKey());
		this.availableStrategies = strats.toArray(new String[0]);
		type = new URISelector("Type", this);
		
		this.reset();
		this.setPreferredSize(new Dimension(800, 600));
		
		//set up the frame
		frame = new JFrame(title + " Strategy Editor");
		frame.setContentPane(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}

	@Override
	public String[] getOptions() {
		return availableStrategies;
	}

	@Override
	public void uriSelected(String uri) {
		
		this.removeAll();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.add(type);
		
		fields = new HashMap<ParserKeys, JTextField>();
		for(Entry<ParserKeys, StrategySelectorFieldType> e : fieldMap.get(uri).entrySet())
		{
			JPanel row = new JPanel();
			row.add(new JLabel(e.getKey().toString() + ":"));
			JTextField field = new JTextField();
			field.setColumns(30);
			row.add(field);
			fields.put(e.getKey(), field);
			this.add(row);
		}
		
		//set up the buttons
		done = new JButton("Done");
		done.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		
		//set up the button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(done);
		buttonPanel.add(cancel);
		this.add(buttonPanel);
		
		this.validate();
		this.updateUI();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(cancel))
			frame.dispose();
		else if(arg0.getSource().equals(done))
		{
			if(!this.isValidConfig())
			{
				JOptionPane.showMessageDialog(frame,
					    "The data is invalid.",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				callBack.setConfigForStrategy(this, this.getData());
				frame.dispose();
			}
		}
	}
	
	@Override
	public boolean isValidConfig()
	{
		if(type == null)
			return false;
		
		if(type.getSelectedURI().equals(""))
			return false;
		for(Entry<ParserKeys, StrategySelectorFieldType> e : fieldMap.get(type.getSelectedURI()).entrySet())
		{
			if(fields == null || fields.get(e.getKey()) == null)
				return false;
			if(e.getValue().equals(StrategySelectorFieldType.numeric))
			{
				Scanner s = new Scanner(fields.get(e.getKey()).getText());
				if(!s.hasNextDouble())
					return false;
			}
			else if(e.getValue().equals(StrategySelectorFieldType.string))
			{
				if(fields.get(e.getKey()).getText().equals(""))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @return	the title of the window
	 */
	public String getTitle()
	{
		return title;
	}

	@Override
	public void setData(Configuration cd) {
		type.setSelectedURI(cd.getString(ParserKeys.type));
		this.uriSelected(type.getSelectedURI());
		for(Entry<ParserKeys, JTextField> e : fields.entrySet())
			e.getValue().setText(cd.getString(e.getKey()));
	}

	@Override
	public void forceSetData(ConfigData cd) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void reset() {
		this.removeAll();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.add(type);
		type.setSelectedURI("");
		
		//set up the cancel button
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		
		//set up the button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(cancel);
		this.add(buttonPanel);
		
		this.validate();
		this.updateUI();
		
	}

	@Override
	public Configuration getData() {
		ConfigData cd = new ConfigData();
		
		if(!isValidConfig())
			return cd;
		
		cd.set(ParserKeys.type, type.getSelectedURI());
		for(Entry<ParserKeys, StrategySelectorFieldType> e : fieldMap.get(type.getSelectedURI()).entrySet())
		{
			if(e.getValue().equals(StrategySelectorFieldType.numeric))
			{
				Scanner s = new Scanner(fields.get(e.getKey()).getText());
				cd.set(e.getKey(), s.nextDouble());
			}
			else if(e.getValue().equals(StrategySelectorFieldType.string))
				cd.set(e.getKey(), fields.get(e.getKey()).getText());
		}
		callBack.setConfigForStrategy(this, cd);
		return cd;
	}

	@Override
	public ConfigType getType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

}
