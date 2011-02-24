package editor.mapitems.body;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import editor.mapitems.body.BodyEditor.Inputs;
import editor.mapitems.body.MapItemDisplay.*;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;

public class BodyEditorNode extends DefaultMutableTreeNode {
	
	public static final Transformation DEFAULT_TRANS = new Transformation(new Position(0, 100), 0);
	
	private MapItemDefinition<? extends MapItem> mid;
	private ShapeDisplay shape;
	private boolean active = false;
	
	public BodyEditorNode(String display, Transformation t)
	{
		super(display);
		shape = new AlignmentStickDisplay(t);
	}
	
	public BodyEditorNode(String display, MapItemDefinition<? extends MapItem> mid, Transformation t, JTextField scale, final Canvas canvas)
	{
		super(display);
		this.mid = mid;
		shape = new MapItemDisplay(mid, scale, t,
				new CanvasDimensionsCallback() {		
					@Override
					public double getWidth() {
						return canvas.getWidth();
					}
					
					@Override
					public double getHeight() {
						return canvas.getHeight();
					}
				});
	}
	
	public BodyEditorNode(String display, ShapeDisplay shape)
	{
		super(display);
		this.shape = shape;
	}
	
	public MapItemDefinition<? extends MapItem> getMapItemDefinition()
	{
		return mid;
	}
	
	public ShapeDisplay getShape()
	{
		return shape;
	}
	
	public void drawShape(Graphics2D g, Position canvasCenter, Position mousePosition, List<Inputs> inputs)
	{
		if(shape != null)
		{
			if(active)
				shape.drawActive(g, canvasCenter, mousePosition, inputs);
			else
				shape.drawInactive(g, canvasCenter);
			
			canvasCenter = canvasCenter.add(shape.getTransformation().getPosition());
			mousePosition = mousePosition.rotateAboutPosition(canvasCenter, -shape.getTransformation().getRotation());
			g.rotate(shape.getTransformation().getRotation(), (int)canvasCenter.getX(), (int)canvasCenter.getY());
		}
		
		for(int i = 0; i < this.getChildCount(); i++)
			((BodyEditorNode)this.getChildAt(i)).drawShape(g, canvasCenter, mousePosition, inputs);
		
		if(shape != null)
			g.rotate(-shape.getTransformation().getRotation(), (int)canvasCenter.getX(), (int)canvasCenter.getY());
	}
	
	public void setActive(boolean a)
	{
		active = a;
	}

}
