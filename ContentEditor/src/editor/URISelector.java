package editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class URISelector extends JPanel
{
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		URISelector uri = new URISelector("Yes", null);
		f.setContentPane(uri);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
		
		uri.getSelectedURI();
	}
	
	private static final long serialVersionUID = 4210934277569886576L;
	
	private static final int WIDTH = 160;
	protected static final int SPACING = 5;
	
	protected JButton button;
	protected JLabel label;
	protected JTextField textField;
	private SelectorOptions options;
	
	public URISelector(String label, SelectorOptions options)
	{
		setOptions(options);
		
		button = new JButton("Select");
		button.addActionListener(new ButtonClickEvent());
		this.label = new JLabel(label);
		
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(WIDTH, 20));
		
		add(this.label);
		add(Box.createHorizontalStrut(SPACING));
		add(textField);
		add(Box.createHorizontalStrut(SPACING));
		add(button);
	}
	
	protected void setOptions(SelectorOptions options)
	{
		this.options = options;
	}
	
	public String getSelectedURI()
	{
		return textField.getText();
	}
	
	public void setSelectedURI(String uri)
	{
		textField.setText(uri);
	}
	
	private class ButtonClickEvent implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			String[] opts = options.getOptions();
			String s = (String) JOptionPane.showInputDialog(
					URISelector.this,
					"Please select a " + label.getText() + ".",
					label.getText() + " Selector",
					JOptionPane.PLAIN_MESSAGE,
					null,
					opts,
					opts[0]);

			// string was returned
			if ((s != null) && (s.length() > 0))
			{
			    textField.setText(s);
			    options.uriSelected(s);
			}
		}
	}
	
	public static interface SelectorOptions
	{
		public String[] getOptions();
		public void uriSelected(String uri);
	}
}
