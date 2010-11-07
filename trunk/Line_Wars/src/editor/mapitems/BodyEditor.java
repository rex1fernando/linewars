package editor.mapitems;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileReader;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.mapItems.MapItemState;
import editor.ConfigurationEditor;
import editor.ListURISelector;
import editor.URISelector;
import editor.URISelector.SelectorOptions;
import editor.animations.Sprite;

public class BodyEditor extends JPanel implements ConfigurationEditor, ActionListener, Runnable, MouseListener {
	
	private MapItemEditor mie;
	
	//variable for selecting the shape type, at the moment only circle supported
	private URISelector shapeType;
	
	//variables for drawing the image
	private Canvas canvas;
	private BufferStrategy strategy;
	private Sprite[] images;
	private long[] imagetimes;
	
	//variable to save the circle (only circles allowed for now)
	private double radius;
	
	//variable for setting the scale
	private JTextField scale;
	
	//buttons to save or cancel
	private JButton done;
	private JButton cancel;
	
	private JFrame frame;
	private boolean running;
	
	private boolean mouseDown;
	
	public BodyEditor(MapItemEditor m)
	{
		mie = m;
		
		//set up the type selector
		shapeType = new URISelector("Type", new ShapeTypeSelector());
		
		//set up the canvas
		canvas = new Canvas();
		canvas.setSize(800, 600);
		
		//set up the scale panel
		scale = new JTextField();
		scale.setColumns(10);
		JPanel scalePanel = new JPanel();
		scalePanel.add(scale);
		scalePanel.add(new JLabel("%"));
		
		//set up the button panel
		done = new JButton("Done");
		done.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(done);
		buttonPanel.add(cancel);
		
		//now set up the whole panel
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(shapeType);
		this.add(canvas);
		this.add(scalePanel);
		this.add(buttonPanel);
		
		//set up the new frame
		frame = new JFrame("Body Editor");
		frame.setContentPane(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		
		running = true;
		
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		
		canvas.setIgnoreRepaint(true);
		canvas.requestFocus();
		canvas.setFocusTraversalKeysEnabled(false);
		canvas.createBufferStrategy(3);
		strategy = canvas.getBufferStrategy();
		
		//lets load the images
		ConfigData animation = null;
		try {
			animation = new ConfigFileReader(mie.getAnimation(MapItemState.Idle)).read();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidConfigFileException e) {
			e.printStackTrace();
		}
		if(animation == null)
			running = false;
		else
		{
			String[] uris = animation.getStringList(ParserKeys.icon).toArray(new String[0]);
			images = new Sprite[uris.length];
			Double[] times = animation.getNumberList(ParserKeys.displayTime).toArray(new Double[0]);
			imagetimes = new long[times.length];
			for(int i = 0; i < uris.length; i++)
			{
				try {
					images[i] = new Sprite(new File(new File(animation.getURI()), uris[i]).getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
					running = false;
					break;
				}
				imagetimes[i] = (long)(double)times[i];
			}
		}
		
		long lastTime = System.currentTimeMillis();
		int currentImage = 0;
		while(running)
		{
			//get graphics object to draw to
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			
			double widthScale = canvas.getWidth()/images[currentImage].getWidth();
			double heightScale = canvas.getHeight()/images[currentImage].getHeight();
			double scale = Math.min(widthScale, heightScale);
			
			images[currentImage].draw(g, 0, 0,
					(int) (images[currentImage].getWidth() * scale),
					(int) (images[currentImage].getHeight() * scale));
			
			if(System.currentTimeMillis() - lastTime > imagetimes[currentImage])
				currentImage = (currentImage + 1)%images.length;
			
			g.dispose();
			strategy.show();
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		frame.dispose();
		
	}

	@Override
	public void setData(ConfigData cd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void forceSetData(ConfigData cd) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public ConfigData getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ParserKeys getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class ShapeTypeSelector implements SelectorOptions {

		@Override
		public String[] getOptions() {
			return new String[]{"Circle"};
		}

		@Override
		public void uriSelected(String uri) {
			// TODO Auto-generated method stub
			
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
