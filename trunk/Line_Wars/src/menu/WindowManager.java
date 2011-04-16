package menu;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import linewars.init.Game;
import menu.ContentProvider.MenuImage;
import menu.panels.CreateGamePanel;
import menu.panels.LoadingScreenPanel;
import menu.panels.OptionsPane;
import menu.panels.TitlePanel;
import editor.BigFrameworkGuy;

public class WindowManager extends JFrame
{	
	private static final long serialVersionUID = 6285963785850922274L;

	private InnerPanel innerPanel;
	
	private TitlePanel titleMenu;
	private CreateGamePanel lobbySystem;
	private LoadingScreenPanel loadingScreen;
	private OptionsPane optionsScreen;
	
	private Image backgroundImage;
	
	public WindowManager()
	{
		super("Titus");
		
		// wait for the background image to load
		ContentProvider.getImageResource(MenuImage.background_title);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setUndecorated(true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		backgroundImage = new BufferedImage(screenSize.width, screenSize.height, BufferedImage.TYPE_INT_RGB);
		
		titleMenu = new TitlePanel(this);
		loadingScreen = new LoadingScreenPanel(this);
		optionsScreen = new OptionsPane(this);
		lobbySystem = new CreateGamePanel(this, optionsScreen);
		
		innerPanel = new InnerPanel(titleMenu);
		setContentPane(innerPanel);
	}
	
	public void showWindow()
	{
		pack();
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setVisible(true);
	}
	
	public Dimension getPanelSize()
	{
		return getContentPane().getSize();
	}
	
	public void gotoTitleMenu()
	{
		if (innerPanel.panel == lobbySystem)
			lobbySystem = new CreateGamePanel(this, optionsScreen);
		
		changeContentPane(titleMenu);
	}
	
	public void gotoCreateGame()
	{
		try {
			lobbySystem.startServer();
			changeContentPane(lobbySystem);
		} catch (IOException e) {
			
		}
	}
	
	public void gotoJoinGame()
	{
		String serverIp = JOptionPane.showInputDialog("Enter the server's ip address:");
		
		if (serverIp != null) {
			try {
				lobbySystem.startClient(serverIp);
				changeContentPane(lobbySystem);
			} catch (SocketException e) {
				JOptionPane.showMessageDialog(this, "Could not connect to the server hosted by " + serverIp);
			}
		}
	}
	
	public void gotoEditor()
	{
		new Thread(new Runnable() {
			public void run() {
				BigFrameworkGuy.main(new String[0]);
			}
		}).start();
	}
	
	public void gotoOptions()
	{
		optionsScreen.loadOptions();
		changeContentPane(optionsScreen);
	}
	
	public void exitGame()
	{
		dispose();
		System.exit(0); //TODO fix this, the server keeps running after the game quits, preventing
						//a new game from starting because it doesn't release its port binding
	}
	
	public void startGame(GameInitializer gameInit)
	{
		gameInit.setWindowManager(this);
		loadingScreen.start(gameInit);
		changeContentPane(loadingScreen);
		try {
			Game g = gameInit.get();
			innerPanel.removeAll();
			changeContentPane(g.getGamePanel());
			g.run();
			loadingScreen.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void changeContentPane(JPanel pane)
	{
		innerPanel.switchPanel(pane);
		validate();
		innerPanel.repaint();
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
	
	private class InnerPanel extends JPanel
	{
		private static final long serialVersionUID = 2587119152843642036L;
		private JPanel panel;
		private MenuImage image;
		
		public InnerPanel(JPanel p)
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			
			int height = (screenSize.height - 640) / 3;
			
			setBackgroundImage(p);
			panel = p;
			add(Box.createVerticalStrut(height));
			add(panel);
		}
		
		public void switchPanel(JPanel newPanel)
		{
			setBackgroundImage(newPanel);
			
			remove(panel);
			panel = newPanel;
			add(panel);
			setBackgroundImage(newPanel);
		}
		
		private void setBackgroundImage(JPanel p)
		{
			if (p == titleMenu) image = MenuImage.background_title;
			else if (p == lobbySystem) image = MenuImage.background_lobby;
			else if (p == loadingScreen) image = MenuImage.background_loading;
			
			Graphics g = backgroundImage.getGraphics();
			
			Image img = ContentProvider.getImageResource(image);
			
			int sx1 = 0, sy1 = 0, sx2 = img.getWidth(null), sy2 = img.getHeight(null);
			double this_ratio = backgroundImage.getWidth(null) / (double) backgroundImage.getHeight(null);
			double img_ratio = sx2 / (double) sy2;
			if (this_ratio < img_ratio) {
				sx1 = (int) ((img.getWidth(null) - img.getHeight(null) * this_ratio) / 2);
				sx2 = img.getWidth(null) - sx1;
			} else {
				sy1 = (int) ((img.getHeight(null) - img.getWidth(null) / this_ratio) / 2);
				sy2 = img.getHeight(null) - sy1;
			}
			
			g.drawImage(img, 0, 0, backgroundImage.getWidth(null), backgroundImage.getHeight(null), sx1, sy1, sx2, sy2, null);
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
		}
	}
}
