package editor.mapitems.body;

import java.awt.Graphics2D;
import java.awt.Point;

import linewars.gamestate.Position;

public interface ShapeDisplay {

	public void drawInactive(Graphics2D g, Position canvasCenter);
	
	public void drawActive(Graphics2D g, Position canvasCenter, Point mousePosition, boolean mouseState);
}
