package editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionListener;

public class ListURISelector extends URISelector
{
	private static final long serialVersionUID = 5603153875399911022L;
	private JList list;
	private Set<String> selections;
	private JButton remove;
	private ListOptions options;
	
	public ListURISelector(String label, SelectorOptions options)
	{
		super(label, null);
		this.options = new ListOptions(options);
		setOptions(this.options);
		removeAll();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		selections = new HashSet<String>();
		initTopPanel();
		
		list = new JList();
		list.setPreferredSize(new Dimension(100, 100));
		add(list);
	}
	
	public void addListSelectionListener(ListSelectionListener lsl)
	{
		list.addListSelectionListener(lsl);
	}
	
	public String[] getHighlightedURIs()
	{
		Object[] objs = list.getSelectedValues();
		String[] uris = new String[objs.length];
		for(int i = 0; i < objs.length; i++)
			uris[i] = (String) objs[i];
		return uris;
	}
	
	public String[] getSelectedURIs()
	{
		return selections.toArray(new String[selections.size()]);
	}
	
	public void setSelectedURIs(String[] uris)
	{
		selections.clear();
		for (String s : uris) selections.add(s);	// add all
		list.setListData(uris);
	}
	
	private void initTopPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		panel.add(label);
		panel.add(Box.createHorizontalStrut(SPACING));
		
		button.setText("Add");
		ActionListener b = button.getActionListeners()[0];
		button.removeActionListener(b);
		button.addActionListener(new AddWrapper(b));
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
	
	private class ListOptions implements SelectorOptions
	{
		private SelectorOptions options;
		
		public ListOptions(SelectorOptions options)
		{
			this.options = options;
		}
		
		@Override
		public String[] getOptions()
		{
			Set<String> ops = new HashSet<String>();
			for (String s : options.getOptions())
			{
				if (!selections.contains(s))
				{
					ops.add(s);
				}
			}
			return ops.toArray(new String[ops.size()]);
		}

		@Override
		public void uriSelected(String uri)
		{
			options.uriSelected(uri);
			selections.add(uri);
			list.setListData(selections.toArray());
		}
	}
	
	private class AddWrapper implements ActionListener
	{
		private ActionListener listener;
		
		public AddWrapper(ActionListener al)
		{
			listener = al;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!(options.getOptions().length == 0))
			{
				listener.actionPerformed(e);
			}
		}
	}
}
