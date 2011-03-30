package menu.components;

import java.awt.Font;

public class CustomFont extends Font
{
	private static final String FONT = "Serif";
	
	public CustomFont(int size)
	{
		super(FONT, Font.BOLD, size);
	}
}
