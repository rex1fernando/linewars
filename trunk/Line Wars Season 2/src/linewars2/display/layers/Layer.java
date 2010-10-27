package linewars2.display.layers;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

interface Layer {
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scaleX, double scaleY);
}
