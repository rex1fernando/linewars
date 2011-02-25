package editor.mapitems.body;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import linewars.display.Animation;
import linewars.display.DisplayConfiguration;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItemState;
import editor.mapitems.body.BodyEditor.DisplayConfigurationCallback;

public class AnimationDrawer {
	
	private static Map<String, Image> images = new HashMap<String, Image>();
	private static String imagePath;
	
	public static void setImagePath(String ip)
	{
		imagePath = ip;
	}
	
	private static Image getImage(String uri)
	{
		if(images.get(uri) == null)
			loadImage(uri);
		return images.get(uri);
	}
	
	private static void loadImage(String uri)
	{
		try {
			images.put(uri, ImageIO.read(new File(new File(imagePath), uri)));
		} catch (IOException e) {
			e.printStackTrace();
			images.put(uri, new BufferedImage(10, 10, BufferedImage.TYPE_3BYTE_BGR));
		}
	}
	
	public static void drawImage(Graphics2D g, Position drawCenter, double rotation, Position canvasSize, 
			Position fitIn, MapItemState state, DisplayConfigurationCallback dcc)
	{
		DisplayConfiguration dc = dcc.getDisplayConfiguration();
		//error check
		if(dc == null || dc.getAnimation(state) == null || dc.getAnimation(state).getNumImages() <= 0)
			return;
		long currentTime = System.currentTimeMillis()%getTotalTime(dc.getAnimation(state));
		int i = 0;
		while(dc.getAnimation(state).getImageTime(i) < currentTime)
		{
			currentTime -= dc.getAnimation(state).getImageTime(i);
			i++;
		}
		
		
		Image toDraw = getImage(dc.getAnimation(state).getImage(i));
		//calculate the width and height scaling and pick the smallest
		double widthScale = (double)fitIn.getX()/toDraw.getWidth(null);
		double heightScale = (double)fitIn.getY()/toDraw.getHeight(null);
		double scale = Math.min(widthScale, heightScale);
		
		Position imageDim = new Position(toDraw.getWidth(null)*scale, toDraw.getHeight(null)*scale);
		Position destUpperLeft = drawCenter.subtract(imageDim.scale(0.5));
		Position destLowerRight = drawCenter.add(imageDim.scale(0.5));
		g.rotate(rotation, (int)drawCenter.getX(), (int)drawCenter.getY());
		g.drawImage(toDraw,
				(int)destUpperLeft.getX(), (int)destUpperLeft.getY(), 
				(int)destLowerRight.getX(), (int)destLowerRight.getY(), 
				0, 0, 
				toDraw.getWidth(null), toDraw.getHeight(null), 
				null);
		g.rotate(-rotation, (int)drawCenter.getX(), (int)drawCenter.getY());
	}
	
	private static long getTotalTime(Animation a)
	{
		long ret = 0;
		for(int i = 0; i < a.getNumImages(); i++)
			ret += a.getImageTime(i);
		return ret;
	}

}
