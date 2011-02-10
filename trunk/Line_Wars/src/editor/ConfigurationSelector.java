package editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import configuration.Configuration;
import editor.BigFrameworkGuy.ConfigType;

public class ConfigurationSelector extends JPanel
{	
	private static final long serialVersionUID = 4210934277569886576L;
	
	private static final int WIDTH = 160;
	protected static final int SPACING = 5;
	
	protected JButton button;
	protected JLabel label;
	protected JTextField textField;
	private BigFrameworkGuy bfg;
	private ConfigType configType;
	private Configuration selectedConfig;
	
	public ConfigurationSelector(String label, BigFrameworkGuy bfg, ConfigType configType)
	{
		this.bfg = bfg;
		this.configType = configType;
		selectedConfig = null;
		
		button = new JButton("Select");
		button.addActionListener(new ButtonClickEvent());
		this.label = new JLabel(label);
		
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(WIDTH, 20));
		textField.setEditable(false);
		
		add(this.label);
		add(Box.createHorizontalStrut(SPACING));
		add(textField);
		add(Box.createHorizontalStrut(SPACING));
		add(button);
	}
	
	public Configuration getSelectedConfiguration()
	{
		return selectedConfig;
	}
	
	public void setSelectedConfiguration(Configuration c)
	{
		selectedConfig = c;
		if(c != null)
			textField.setText((String) c.getPropertyForName("bfgName").getValue());
		else
			textField.setText("");
	}
	
	private class ButtonClickEvent implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			SelectorOption[] opts = createOptions(configType);
			SelectorOption s = (SelectorOption) JOptionPane.showInputDialog(
					ConfigurationSelector.this,
					"Please select a " + label.getText() + ".",
					label.getText() + " Selector",
					JOptionPane.PLAIN_MESSAGE,
					null,
					opts,
					opts[0]);

			if (s != null)
			{
			    textField.setText(s.toString());
			    selectedConfig = s.getConfiguration();
			}
		}
	}
	
	protected SelectorOption[] createOptions(ConfigType configType)
	{
		List<Configuration> configs = bfg.getConfigurationsByType(configType);
		SelectorOption[] options = new SelectorOption[configs.size()];
		for (int i = 0; i < configs.size(); ++i)
		{
			options[i] = new SelectorOption(configs.get(i));
		}
		return options;
	}
	
	protected class SelectorOption
	{
		private Configuration config;
		private String label;
		
		public SelectorOption(Configuration c)
		{
			config = c;
			this.label = (String) c.getPropertyForName("bfgName").getValue();
		}
		
		public Configuration getConfiguration()
		{
			return config;
		}
		
		@Override
		public String toString()
		{
			return label;
		}
	}
}

