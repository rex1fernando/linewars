package menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import linewars.gamestate.MapConfiguration;
import linewars.gamestate.Race;

public class ContentProvider
{
	private static final Image BLANK_IMAGE;
	
	private static final ResourceLoader loader;
	
	private static Map<MenuImage, Future<Image>> imageResources;
	private static Map<Integer, Future<Image>> mapImages;
	private static Map<MenuImage, String> filenames;
	
	private static final Race[] races;
	public static final Font FONT;

	// loads the blank image
	static
	{
		BLANK_IMAGE = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		Graphics g = BLANK_IMAGE.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, BLANK_IMAGE.getWidth(null), BLANK_IMAGE.getHeight(null));
	}
	
	// initializes the map of filenames
	static
	{
		filenames = new HashMap<MenuImage, String>();
		
		// button icons
		filenames.put(MenuImage.menu_button_default, "resources/ui/components/button.png");
		filenames.put(MenuImage.menu_button_rollover, "resources/ui/components/button_highlight.png");
		filenames.put(MenuImage.lobby_button_default, "resources/ui/components/options_button.png");
		filenames.put(MenuImage.lobby_button_rollover, "resources/ui/components/options_button_highlight.png");
		filenames.put(MenuImage.options_button_default, "resources/ui/components/options_button.png");
		filenames.put(MenuImage.options_button_rollover, "resources/ui/components/options_button_highlight.png");
		
		// concept art
		filenames.put(MenuImage.background_title, "resources/ui/backgrounds/title_menu.png");
		filenames.put(MenuImage.background_lobby, "resources/ui/backgrounds/lobby_system.png");
		filenames.put(MenuImage.lobby_back, "resources/ui/backgrounds/lobby_back.png");
		filenames.put(MenuImage.background_loading, "resources/ui/backgrounds/loading_screen.png");
		filenames.put(MenuImage.options_back, "resources/ui/backgrounds/options_back.png");
		
		// combo box art
		filenames.put(MenuImage.combobox_button, "resources/ui/components/combobox_button.png");
		filenames.put(MenuImage.combobox_main, "resources/ui/components/combobox_main.png");
		filenames.put(MenuImage.combobox_background, "resources/ui/components/combobox_background.png");
		filenames.put(MenuImage.combobox_highlighted, "resources/ui/components/combobox_highlight.png");
		
		// scrollbar
		filenames.put(MenuImage.scrollbar_track, "resources/ui/components/scrollbar_track.png");
		filenames.put(MenuImage.scrollbar_highlight, "resources/ui/components/scrollbar_highlight.png");
		filenames.put(MenuImage.scrollbar_thumb, "resources/ui/components/scrollbar_thumb.png");
		filenames.put(MenuImage.scrollbar_incr, "resources/ui/components/scrollbar_incr.png");
		filenames.put(MenuImage.scrollbar_decr, "resources/ui/components/scrollbar_decr.png");
		
		// loading screen
		filenames.put(MenuImage.loading_spinner, "resources/ui/components/loading_spinner.png");
		
		// slider
		filenames.put(MenuImage.slider_thumb, "resources/ui/components/slider_thumb.png");
		filenames.put(MenuImage.slider_track, "resources/ui/components/slider_track.png");
		
		// progress bar
		filenames.put(MenuImage.progressbar_back, "resources/ui/components/loadingbar_back.png");
		filenames.put(MenuImage.progressbar_front, "resources/ui/components/loadingbar_front.png");
		
		// initialize image resources
		clearImageResources();
		
		// init loader
		loader = new ResourceLoader();
		loader.prioritize(MenuImage.background_title);
		loader.prioritize(MenuImage.menu_button_rollover);
		loader.prioritize(MenuImage.menu_button_default);
		loader.execute();
	}
	
	// initialize races
	static
	{
		String from = "resources/races";
		String ext = "cfg";
		Object[] objs = deserializeObjects(from, ext);
		List<Race> maps = new ArrayList<Race>();
		for (int i = 0; i < objs.length; ++i)
		{
			try {
				Race r = (Race) objs[i];
				maps.add(r);
			} catch (ClassCastException e) {}
		}
		races = maps.toArray(new Race[0]);
	}
	
	// initialize font
	static
	{
		Font temp = null;
	    try {
			temp = Font.createFont(Font.TRUETYPE_FONT, new File("resources/ui/font.ttf"));
		} catch (Exception e) { e.printStackTrace();}
		FONT = temp.deriveFont(Font.BOLD);
	}
	
	public enum MenuImage
	{
		blank,
		
		lobby_back,
		lobby_button_default,
		lobby_button_rollover,
		
		options_back,
		options_button_default,
		options_button_rollover,
		
		menu_button_default,
		menu_button_rollover,
		
		combobox_button,
		combobox_main,
		combobox_background,
		combobox_highlighted,
		
		scrollbar_highlight,
		scrollbar_track,
		scrollbar_thumb,
		scrollbar_incr,
		scrollbar_decr,
		
		background_title,
		background_lobby,
		background_loading,
		
		loading_spinner,
		
		slider_thumb,
		slider_track,
		
		progressbar_back,
		progressbar_front
	}
	
	public static Point centerText(FontMetrics f, String text, int width, int height)
	{
		int w = f.stringWidth(text);
		int h = f.getAscent();

		Point p = new Point();
		p.x = (int) ((width - w) / 2);
		p.y = (int) ((height - h) / 2) + h;
		return p;
	}
	
	public static Race[] getAvailableRaces()
	{
		return races;
	}
	
	public static Color[] getAvailableColors()
	{
		Color[] colors = new Color[] {
				new Color(140, 23, 23), // scarlet
				Color.blue,
				Color.green,
				Color.orange,
				Color.yellow,
				Color.pink,
				Color.cyan,
				Color.magenta,
				new Color(0, 128, 128), // teal
				new Color(0, 0, 128), // navy
				new Color(0, 245, 255), // turquoise
				new Color(47, 79, 47) // dark green
		};
		
		return colors;
	}
	
	public static Object[] getAvailableReplays() // FIXME
	{
		String from = "resources/replays";
		String ext = "replay";
		//return (Object[]) deserializeObjects(from, ext);
		return new String[] {"Replay 1", "Replay 2" };
	}
	
	public static MapConfiguration[] getAvailableMaps()
	{	
		String from = "resources/maps";
		String ext = "cfg";
		Object[] objs = deserializeObjects(from, ext);
		List<MapConfiguration> maps = new ArrayList<MapConfiguration>();
		for (int i = 0; i < objs.length; ++i)
		{
			try {
				MapConfiguration m = (MapConfiguration) objs[i];
				maps.add(m);
			} catch (ClassCastException e) {}
		}
		
		return maps.toArray(new MapConfiguration[0]);
	}
	
	public static Image getMapImage(int map)
	{
		try {
			return mapImages.get(map).get();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static synchronized Image getImageResource(MenuImage img)
	{
		loader.prioritize(img);
		try {
			return imageResources.get(img).get();
		} catch (Exception e) {
			return BLANK_IMAGE;
		}
	}
	
	public static synchronized void clearImageResources()
	{
		if (imageResources != null) imageResources.clear();
		if (mapImages != null) mapImages.clear();
		
		imageResources = new HashMap<MenuImage, Future<Image>>();
		mapImages = new HashMap<Integer, Future<Image>>();
		
		for (MenuImage key : filenames.keySet())
		{
			imageResources.put(key, new ImageProxy(filenames.get(key)));
		}
		
		MapConfiguration[] maps = getAvailableMaps();
		Dimension size = new Dimension(250, 250);
		for (int i = 0; i < maps.length; ++i)
		{
			String filename = "resources/images/" + maps[i].getImageURI();
			mapImages.put(i, new ImageProxy(filename, size));
		}
		
		MapImageLoader loader = new MapImageLoader();
		loader.execute();
	}
	
	private static Object[] deserializeObjects(String from, String extension)
	{
		File[] files = getFiles(from, extension);
		List<Object> objs = new ArrayList<Object>();
		for (int i = 0; i < files.length; ++i)
		{
			Object o = deserialize(files[i]);
			if (o != null)
				objs.add(o);
		}
		return objs.toArray();
	}
	
	private static File[] getFiles(String from, String extension)
	{
		File dir = new File(from);
		if (dir.isDirectory() == false) throw new IllegalArgumentException("The path given is not a directory!");
		
		File[] allFiles = dir.listFiles();
		List<File> selFiles = new ArrayList<File>();
		for (File f : allFiles)
		{
			if (f.getName().endsWith("." + extension))
				selFiles.add(f);
		}
		
		return selFiles.toArray(new File[0]);
	}
	
	private static Object deserialize(File f)
	{
		Object obj = null;
		
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			obj = ois.readObject();
		} catch (Exception e) {}
		
		return obj;
	}
	
	private static class MapImageLoader extends SwingWorker<Object, Object>
	{
		@Override
		protected Object doInBackground() throws Exception {
			for (int i = 0; i < mapImages.size(); ++i)
			{
				ImageProxy img = (ImageProxy) mapImages.get(i);
				img.run();
			}
			return null;
		}
	}
	
	private static class ResourceLoader extends SwingWorker<Object, Object>
	{
		private Deque<MenuImage> queue;
		private Object lock;
		
		public ResourceLoader()
		{
			initQueue();
			lock = new Object();
		}
		
		private void initQueue()
		{
			queue = new ArrayDeque<MenuImage>(filenames.size());
			for (MenuImage key : filenames.keySet())
			{
				queue.offer(key);
			}
		}
		
		public void prioritize(MenuImage key)
		{
			synchronized (lock)
			{
				if (queue.contains(key))
				{
					queue.remove(key);
					queue.offerFirst(key);
				}
			}
		}
		
		@Override
		protected Object doInBackground() throws Exception
		{
			while (queue.isEmpty() == false)
			{
				MenuImage key = null;
				synchronized (lock) {
					key = queue.poll();
				}
				ImageProxy img = (ImageProxy) imageResources.get(key);
				img.run();
			}
			
			return null;
		}
	}
	
	private static class ImageProxy extends FutureTask<Image>
	{
		public ImageProxy(String filename)
		{
			super(new ImageLoader(filename));
		}
		
		public ImageProxy(String filename, Dimension size)
		{
			super(new ImageLoader(filename, size));
		}
	}
	
	private static class ImageLoader implements Callable<Image>
	{
		private String filename;
		private Dimension size;
		
		public ImageLoader(String filename)
		{
			this(filename, null);
		}
		
		public ImageLoader(String filename, Dimension size)
		{
			this.filename = filename;
			this.size = size;
		}
		
		@Override
		public Image call() throws Exception
		{
			Image img = ImageIO.read(new File(filename));
			
			if (size != null)
			{
				Image newImg = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
				Graphics g = newImg.getGraphics();
				
				g.setColor(Color.black);
				g.fillRect(0, 0, newImg.getWidth(null), newImg.getHeight(null));
				
				double thisWidth = newImg.getWidth(null);
				double thisHeight = newImg.getHeight(null);
				double thatWidth = img.getWidth(null);
				double thatHeight = img.getHeight(null);
				
				double thisRatio = thisWidth / thisHeight;
				double thatRatio = thatWidth / thatHeight * 1.0;
				
				double scale = (thisRatio > thatRatio) ? thisHeight / thatHeight : thisWidth / thatWidth;
				
				int imgWidth = (int) (thatWidth * scale);
				int imgHeight = (int) (thatHeight * scale);
				int x = (int) (thisWidth - imgWidth) / 2;
				int y = (int) (thisHeight - imgHeight) / 2;
				
				g.drawImage(img, x, y, imgWidth, imgHeight, null);
				
				return newImg;
			}
			else
			{
				return img;
			}
		}		
	}
}
