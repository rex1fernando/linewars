package menu;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import menu.ContentProvider.MenuImage;
import menu.panels.CreateGamePanel;
import menu.panels.TitlePanel;

public class WindowManager extends JFrame
{	
	private static final long serialVersionUID = 6285963785850922274L;

	private InnerPanel innerPanel;
	
	private TitlePanel titleMenu;
	private CreateGamePanel createMenu;
	
	public WindowManager()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		
		titleMenu = new TitlePanel(this);
		createMenu = new CreateGamePanel(this);
		
		innerPanel = new InnerPanel(titleMenu);
		setContentPane(innerPanel);
	}
	
	public void showWindow()
	{
		pack();
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		
		setVisible(true);
		//setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
	
	public Dimension getPanelSize()
	{
		return getContentPane().getSize();
	}
	
	public void gotoTitleMenu()
	{
		changeContentPane(titleMenu);
	}
	
	public void gotoCreateGame()
	{
		createMenu.startServer();

		changeContentPane(createMenu);
	}
	
	public void gotoJoinGame()
	{
		String serverIp = JOptionPane.showInputDialog("Enter the server's ip address:");
		
		if (serverIp != null) {
			createMenu.startClient(serverIp);
			changeContentPane(createMenu);
		}
	}
	
	public void gotoEditor()
	{
		// TODO implement
	}
	
	public void gotoOptions()
	{
		// TODO implement
	}
	
	public void gotoCredits()
	{
		// TODO implement
	}
	
	public void exitGame()
	{
		dispose();
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
			setBackgroundImage(p);
			panel = p;
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
			if (p == titleMenu) image = MenuImage.titleBackground;
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			Image img = ContentProvider.getImageResource(image);
//			Image flt = null;
//			try {
//				flt = ImageIO.read(new File("resources/ui/OverBar1.png"));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			int sx1 = 0, sy1 = 0, sx2 = img.getWidth(null), sy2 = img.getHeight(null);
			double this_ratio = getWidth() / (double) getHeight();
			double img_ratio = sx2 / (double) sy2;
			if (this_ratio < img_ratio) {
				sx1 = (int) ((img.getWidth(null) - img.getHeight(null) * this_ratio) / 2);
				sx2 = img.getWidth(null) - sx1;
			} else {
				sy1 = (int) ((img.getHeight(null) - img.getWidth(null) / this_ratio) / 2);
				sy2 = img.getHeight(null) - sy1;
			}
			
			g.drawImage(img, 0, 0, getWidth(), getHeight(), sx1, sy1, sx2, sy2, null);
//			g.drawImage(flt, 0, 0, getWidth(), getHeight(), 0, 0, flt.getWidth(null), flt.getHeight(null), null);
		}
	}
}
