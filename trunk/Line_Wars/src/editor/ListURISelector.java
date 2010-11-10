package editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;

public class ListURISelector extends URISelector implements ListSelectionListener
{	
	public static interface ListSelectorOptions extends SelectorOptions
	{
		public String[] getOptions();
		public void uriSelected(String uri);
		public void uriRemoved(String uri);
		public void uriHighlightChange(String[] uris);
	}
	
	private static final long serialVersionUID = 5603153875399911022L;
	private JList list;
	private Set<String> selections;
	private JButton remove;
	private ListOptions options;
	
	public ListURISelector(String label, ListSelectorOptions options)
	{
		super(label, null);
		this.options = new ListOptions(options);
		setOptions(this.options);
		removeAll();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		selections = new HashSet<String>();
		initTopPanel();
		
		list = new JList();
		Dimension size = new Dimension(400, 75);
		list.setPreferredSize(size);
		list.setMinimumSize(size);
		list.addListSelectionListener(this);
		
		JScrollPane scroll = new JScrollPane(list);
		scroll.setPreferredSize(size);
		scroll.setMaximumSize(size);
		scroll.setMinimumSize(size);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		add(scroll);
	}
	
	public String[] getHighlightedURIs()
	{
		Object[] objs = list.getSelectedValues();
		String[] uris = new String[objs.length];
		for(int i = 0; i < objs.length; i++)
			uris[i] = (String) objs[i];
		return uris;
	}
	
	public void setHighlightedURIs(String[] uris){
		//clear current selection
		list.clearSelection();
		
		//get list of all things in the list
		String[] allURIs = new String[list.getModel().getSize()];
		for(int i = 0; i < allURIs.length; i++){
			allURIs[i] = (String) list.getModel().getElementAt(i);
		}
		
		//figure out which Strings are in both lists
		//and highlight them
		for(int i = 0; i < allURIs.length; i++){
			for(int j = 0; j < uris.length; j++){
				if(uris[j].equals(allURIs[i])){
					//highlight this String
					list.addSelectionInterval(i, i);
				}
			}
		}
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
				options.uriRemoved((String) o);
			}
			
			list.setListData(selections.toArray());
		}
	}
	
	private class ListOptions implements ListSelectorOptions
	{
		private ListSelectorOptions options;
		
		public ListOptions(ListSelectorOptions options)
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

		@Override
		public void uriRemoved(String uri) {
			options.uriRemoved(uri);		
		}

		@Override
		public void uriHighlightChange(String[] uris) {
			options.uriHighlightChange(uris);
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

	@Override
	public void valueChanged(ListSelectionEvent e) {
		List<String> selection = new ArrayList<String>();
		for (Object o : list.getSelectedValues())
		{
			selection.add((String) o);
		}
		options.uriHighlightChange(selection.toArray(new String[0]));
		
	}
}
