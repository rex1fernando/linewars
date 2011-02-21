package editor.mapitems.body;

import java.awt.Shape;

import javax.swing.tree.DefaultMutableTreeNode;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;

public class BodyEditorNode extends DefaultMutableTreeNode {
	
	private MapItemDefinition<? extends MapItem> mid;
	private ShapeDisplay shape;
	
	public BodyEditorNode(String display)
	{
		super(display);
	}
	
	public BodyEditorNode(String display, MapItemDefinition<? extends MapItem> mid)
	{
		this(display);
		this.mid = mid;
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

}
