package editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import configuration.Configuration;
import editor.BigFrameworkGuy.ConfigType;

public class ListConfigurationSelector extends ConfigurationSelector
{	
	private static final long serialVersionUID = 5603153875399911022L;
	private JList list;
	private Set<Configuration> selections;
	private JButton remove;
	
	public ListConfigurationSelector(String label, BigFrameworkGuy bfg, ConfigType configType)
	{
		super(label, bfg, configType);
		removeAll();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		selections = new HashSet<Configuration>();
		initTopPanel();
		
		list = new JList();
		Dimension size = new Dimension(400, 75);
		list.setPreferredSize(size);
		list.setMinimumSize(size);
		
		JScrollPane scroll = new JScrollPane(list);
		scroll.setPreferredSize(size);
		scroll.setMaximumSize(size);
		scroll.setMinimumSize(size);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		add(scroll);
	}
	
	public Configuration[] getSelectedConfigurations()
	{
		return selections.toArray(new Configuration[selections.size()]);
	}
	
	public void setSelectedConfigurations(List<? extends Configuration> configs)
	{
		selections.clear();
		selections.addAll(configs);
		list.setListData(createOptions(configs));
	}
	
	private SelectorOption[] createOptions(List<Configuration> configs)
	{
		SelectorOption[] options = new SelectorOption[configs.size()];
		for (int i = 0; i < configs.size(); ++i)
		{
			options[i] = new SelectorOption(configs.get(i));
		}
		return options;
	}
	
	private void initTopPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		panel.add(label);
		panel.add(Box.createHorizontalStrut(SPACING));
		
		button.setText("Add");
		panel.add(button);
		panel.add(Box.createHorizontalStrut(SPACING));
		
		remove = new JButton("Remove");
		remove.addActionListener(new RemoveButtonListener());
		panel.add(remove);
		
		add(panel);
	}
	
	private class RemoveButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			for (Object o : list.getSelectedValues())
			{
				selections.remove(o);
			}
			list.setListData(selections.toArray());
		}
	}
}

