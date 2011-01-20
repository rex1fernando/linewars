package menu.creategame;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import menu.CustomFont;
import menu.WindowManager;

public class CreateGamePanel extends JPanel
{
	private static final Font LABEL_FONT = new CustomFont(18);
	
	private WindowManager parent;
	private NamePanel namePanel;
	private MapAndReplayPanel mapReplayPanel;
	
	public CreateGamePanel(WindowManager parent)
	{
		this.parent = parent;
		setSize(parent.getPanelSize());
		
		BoxLayout mgr = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(mgr);
		setBorder(BorderFactory.createEmptyBorder(400, 200, 200, 400));
		
		setOpaque(false);
		initComponents();
	}
	
	private void initComponents()
	{
		namePanel = new NamePanel();
		add(namePanel);
		
		mapReplayPanel = new MapAndReplayPanel();
		add(mapReplayPanel);
		
		JPanel middlePanel = new JPanel();
		add(middlePanel);
		
		add(new ButtonPanel());
	}
	
	private class NamePanel extends JPanel
	{
		private static final String TEXT = "Name";
		
		private JLabel nameLabel;
		private JTextField nameField;
		
		public NamePanel()
		{
			setAlignmentX(RIGHT_ALIGNMENT);
			
			nameLabel = new JLabel(TEXT);
			nameLabel.setFont(LABEL_FONT);
			nameField = new JTextField();
			
			add(nameLabel);
			add(Box.createHorizontalStrut(5));
			add(nameField);
		}
	}
	
	private class MapAndReplayPanel extends JPanel implements ItemListener
	{
		private JLabel checkBoxLabel;
		private JCheckBox checkBox;
		private JLabel comboBoxLabel;
		private String[] labelValues = { "Map", "Game" };
		private JComboBox comboBox;
		
		public MapAndReplayPanel()
		{
			setAlignmentX(RIGHT_ALIGNMENT);
			
			checkBoxLabel = new JLabel("Replay");
			checkBoxLabel.setFont(LABEL_FONT);
			checkBox = new JCheckBox();
			comboBoxLabel = new JLabel(labelValues[0]);
			comboBoxLabel.setFont(LABEL_FONT);
			comboBox = new JComboBox();
			comboBox.setEditable(false);
			
			checkBox.addItemListener(this);
			
			add(checkBoxLabel);
			add(Box.createHorizontalStrut(5));
			add(checkBox);
			add(Box.createHorizontalStrut(20));
			add(comboBoxLabel);
			add(Box.createHorizontalStrut(5));
			add(comboBox);
		}

		@Override
		public void itemStateChanged(ItemEvent e)
		{
			if (checkBox.isSelected())
				updateComboBox(labelValues[1], getReplays());
			else
				updateComboBox(labelValues[0], getMaps());
		}
		
		private void updateComboBox(String label, Object[] items)
		{
			comboBoxLabel.setText(label);
			comboBox.removeAllItems();
			for (Object item : items)
			{
				comboBox.addItem(item);
			}
		}
		
		private String[] getReplays()
		{
			// TODO implement
			return new String[0];
		}
		
		private String[] getMaps()
		{
			// TODO implement
			return new String[0];
		}
	}
	
	private class ButtonPanel extends JPanel implements ActionListener
	{
		private JButton start;
		private JButton cancel;
		
		public ButtonPanel()
		{
			setAlignmentX(LEFT_ALIGNMENT);
			
			start = new JButton("Start");
			start.setFont(LABEL_FONT);
			start.addActionListener(this);
			cancel = new JButton("Cancel");
			cancel.setFont(LABEL_FONT);
			cancel.addActionListener(this);
			
			add(start);
			add(Box.createHorizontalStrut(10));
			add(cancel);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == start)
			{
				// TODO start the game
			}
			else if (e.getSource() == cancel)
			{
				parent.gotoTitleMenu();
			}
		}
	}
}
