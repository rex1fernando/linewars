package editor.mapitems.body;

import java.awt.Graphics2D;
import java.util.List;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.configurations.ShapeConfiguration;
import editor.mapitems.body.BodyEditor.Inputs;

public interface ShapeDisplay {

	public void drawInactive(Graphics2D g, Position canvasCenter, Position canvasSize, double scale);
	
	public void drawActive(Graphics2D g, Position canvasCenter, Position mousePosition, List<Inputs> inputs, Position canvasSize, double scale);
	
	public ShapeConfiguration generateConfiguration(double scalingFactor);
	
	public Transformation getTransformation();
}
