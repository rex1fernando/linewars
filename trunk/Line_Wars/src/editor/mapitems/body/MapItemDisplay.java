package editor.mapitems.body;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.shapes.configurations.CircleConfiguration;
import linewars.gamestate.shapes.configurations.RectangleConfiguration;
import linewars.gamestate.shapes.configurations.ShapeAggregateConfiguration;
import linewars.gamestate.shapes.configurations.ShapeConfiguration;
import editor.mapitems.body.BodyEditor.Inputs;

public class MapItemDisplay implements ShapeDisplay {
	
	private MapItemDefinition<?> definition;
	
	private AlignmentStickDisplay aligner;
	
	private double currentScale = 1;
	
	public MapItemDisplay(MapItemDefinition<?> mid, Transformation trans)
	{
		this.definition = mid;
		aligner = new AlignmentStickDisplay(trans);
	}

	@Override
	public void drawInactive(Graphics2D g, Position canvasCenter, Position canvasSize, double scale) {
		currentScale = scale;
		drawShapes(g, canvasCenter, canvasSize, Color.gray);
	}

	@Override
	public void drawActive(Graphics2D g, Position canvasCenter,
			Position mousePosition, List<Inputs> inputs, Position canvasSize, double scale) {
		currentScale = scale;
		drawShapes(g, canvasCenter, canvasSize, Color.red);
		
		aligner.drawActive(g, canvasCenter, mousePosition, inputs, canvasSize, scale);
	}
	
	@Override
	public ShapeConfiguration generateConfiguration(double scalingFactor) {
		throw new UnsupportedOperationException();
	}
	
	private void drawShapes(Graphics2D g, Position canvasCenter, Position canvasSize, Color c)
	{
		Position actualCenter = canvasCenter.add(aligner.getTransformation().getPosition());
		g.rotate(aligner.getTransformation().getRotation(), (int)actualCenter.getX(), (int)actualCenter.getY());
		
		ShapeConfiguration sc = definition.getBodyConfig();
		if(sc instanceof CircleConfiguration)
			drawCircle(g, canvasCenter, (CircleConfiguration) sc, c);
		else if(sc instanceof RectangleConfiguration)
			drawRect(g, canvasCenter, (RectangleConfiguration) sc, c);
		else if(sc instanceof ShapeAggregateConfiguration)
		{
			for(String name : ((ShapeAggregateConfiguration)sc).getDefinedShapeNames())
			{
				ShapeConfiguration subConfig = ((ShapeAggregateConfiguration)sc).getShapeConfigurationForName(name);
				if(subConfig instanceof CircleConfiguration)
					drawCircle(g, canvasCenter, (CircleConfiguration) subConfig, c);
				else if(subConfig instanceof RectangleConfiguration)
					drawRect(g, canvasCenter, (RectangleConfiguration) subConfig, c);
			}
		}
		
		g.rotate(-aligner.getTransformation().getRotation(), (int)actualCenter.getX(), (int)actualCenter.getY());
	}
	
	private void drawCircle(Graphics2D g, Position canvasCenter, CircleConfiguration cc, Color c)
	{
		g.setColor(new Color(c.getRed(), c.getGreen(),
				c.getBlue(), 128));
		Position upperLeft = canvasCenter.add(aligner.getTransformation().getPosition())
				.add(cc.getPosition().getPosition().scale(1/currentScale))
				.subtract(new Position(cc.getRadius()/currentScale, cc.getRadius()/currentScale));
		Position lowerRight = new Position(cc.getRadius()/currentScale, cc.getRadius()/currentScale).scale(2);
		g.fillOval((int)upperLeft.getX(), (int)upperLeft.getY(),
					(int)lowerRight.getX(), (int)lowerRight.getY());
		
		g.setColor(c);
		g.setStroke(new BasicStroke(3));
		g.drawOval((int)upperLeft.getX(), (int)upperLeft.getY(),
				(int)lowerRight.getX(), (int)lowerRight.getY());
	}
	
	private void drawRect(Graphics2D g, Position canvasCenter, RectangleConfiguration rc, Color c)
	{
		Position upperLeft = canvasCenter.add(aligner.getTransformation().getPosition())
				.add(rc.getPosition().getPosition().scale(1/currentScale))
				.subtract(rc.getWidth()/currentScale / 2, rc.getHeight()/currentScale / 2);
		Position rectCenter = upperLeft.add(rc.getWidth()/currentScale/2, rc.getHeight()/currentScale/2);
		g.rotate(rc.getPosition().getRotation(), (int)rectCenter.getX(), (int)rectCenter.getY());
		
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 128));
		g.fillRect((int)upperLeft.getX(), (int)upperLeft.getY(), 
				(int)(rc.getWidth()/currentScale), (int)(rc.getHeight()/currentScale));
		
		g.setColor(c);
		g.setStroke(new BasicStroke(3));
		g.drawRect((int)upperLeft.getX(), (int)upperLeft.getY(), 
				(int)(rc.getWidth()/currentScale), (int)(rc.getHeight()/currentScale));
		g.rotate(-rc.getPosition().getRotation(), (int)rectCenter.getX(), (int)rectCenter.getY());
	}

	@Override
	public Transformation getTransformation() {
		return aligner.getTransformation();
	}

}
