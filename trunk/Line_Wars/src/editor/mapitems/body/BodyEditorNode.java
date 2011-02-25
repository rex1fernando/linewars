package editor.mapitems.body;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import linewars.display.DisplayConfiguration;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.shapes.Rectangle;
import editor.mapitems.body.BodyEditor.DisplayConfigurationCallback;
import editor.mapitems.body.BodyEditor.Inputs;

public class BodyEditorNode extends DefaultMutableTreeNode {
	
	public static final Transformation DEFAULT_TRANS = new Transformation(new Position(0, 100), 0);
	
	private MapItemDefinition<? extends MapItem> mid;
	private ShapeDisplay shape;
	private boolean active = false;
	private boolean enabled = true;
	
	private BodyEditorNode(String display)
	{
		setUserObject(display);
	}
	
	public BodyEditorNode(String display, Transformation t)
	{
		this(display);
		shape = new AlignmentStickDisplay(t);
	}
	
	public BodyEditorNode(String display, MapItemDefinition<? extends MapItem> mid, Transformation t, final Canvas canvas)
	{
		this(display);
		this.mid = mid;
		shape = new MapItemDisplay(mid, t);
	}
	
	public BodyEditorNode(String display, ShapeDisplay shape)
	{
		this(display);
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
	
	public void drawShape(Graphics2D g, Position canvasCenter, Position canvasSize, Position mousePosition, List<Inputs> inputs, double scale)
	{
		if(shape != null)
		{
			//draw the animations
			if(mid != null)
			{
				Position fitIn = new Position(((DisplayConfiguration)mid.getDisplayConfiguration()).getDimensions().getX(), 
						((DisplayConfiguration)mid.getDisplayConfiguration()).getDimensions().getY()).scale(1/scale);
				AnimationDrawer.drawImage(g, canvasCenter.add(shape.getTransformation().getPosition()), 
						shape.getTransformation().getRotation(), canvasSize, fitIn, MapItemState.Idle, 
						new DisplayConfigurationCallback() {			
							@Override
							public DisplayConfiguration getDisplayConfiguration() {
								return (DisplayConfiguration) mid.getDisplayConfiguration();
							}
						});
			}
			
			if(active)
				shape.drawActive(g, canvasCenter, mousePosition, inputs, canvasSize, scale);
			else
				shape.drawInactive(g, canvasCenter, canvasSize, scale);
			
			canvasCenter = canvasCenter.add(shape.getTransformation().getPosition());
			
			mousePosition = mousePosition.rotateAboutPosition(canvasCenter, -shape.getTransformation().getRotation());
			g.rotate(shape.getTransformation().getRotation(), (int)canvasCenter.getX(), (int)canvasCenter.getY());
		}
		
		for(int i = 0; i < this.getChildCount(); i++)
			((BodyEditorNode)this.getChildAt(i)).drawShape(g, canvasCenter, canvasSize, mousePosition, inputs, scale);
		
		if(shape != null)
			g.rotate(-shape.getTransformation().getRotation(), (int)canvasCenter.getX(), (int)canvasCenter.getY());
	}
	
	public void setActive(boolean a)
	{
		active = a;
	}
	
	public void setEnabled(boolean b)
	{
		enabled = b;
		setUserObject(getUserObject());
	}
	
	public boolean getEnabled()
	{
		return enabled;
	}
	
	@Override
	public void setUserObject(Object o)
	{
		if(enabled)
			super.setUserObject("E: " + o.toString());
		else
			super.setUserObject("D: " + o.toString());
	}
	
	@Override
	public Object getUserObject()
	{
		return super.getUserObject().toString().substring(3);
	}

}
