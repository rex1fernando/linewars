package menu.components;

import javax.swing.JScrollPane;

public class MenuScrollPane extends JScrollPane
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2371010977460493418L;

	public MenuScrollPane()
	{
		this.viewport.setOpaque(false);
		setOpaque(false);
		setVerticalScrollBar(new CustomScrollBar());
		setBorder(null);
	}
}