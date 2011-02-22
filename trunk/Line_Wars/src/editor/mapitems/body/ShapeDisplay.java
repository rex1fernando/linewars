package editor.mapitems.body;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import linewars.gamestate.Position;
import editor.mapitems.body.BodyEditor.Inputs;

public interface ShapeDisplay {

	public void drawInactive(Graphics2D g, Position canvasCenter);
	
	public void drawActive(Graphics2D g, Position canvasCenter, Point mousePosition, List<Inputs> inputs);
}
