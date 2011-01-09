package menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class TitlePanel extends JPanel
{
	private static final int VSPACE = 100;
	private static final int HSPACE = 200;
	private static final int VGAP = 25;
	
	private long gameStartTime;
	private WindowManager parent;
	
	public TitlePanel(WindowManager parent)
	{
		this.parent = parent;
		gameStartTime = System.currentTimeMillis();
		setSize(parent.getPanelSize());
		
		GridLayout mgr = new GridLayout(7, 1);
		mgr.setVgap(VGAP);
		setLayout(mgr);
		
		setOpaque(false);
		
		initComponents();
	}
	
	@Override
	public void paint(Graphics g)
	{
		// g.drawImage(BACK_ANIMATION.getImage(getTime(), 0), 0, 0, getWidth(), getHeight(), null);
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		super.paint(g);
	}
	
	private void initComponents()
	{
		add(Box.createRigidArea(new Dimension(0, VSPACE)));
		createButton("Create Game", "create");
		createButton("Join Game", "join");
		createButton("Launch Editor", "editor");
		createButton("Credits", "credits");
		createButton("Exit Game", "exit");
		add(Box.createRigidArea(new Dimension(0, VSPACE)));
	}
	
	private void createButton(String text, String action)
	{
		JButton button = new JButton(text);
		button.setIcon(new ButtonIcon(button));
		
		button.setActionCommand(action);
		button.addActionListener(new ButtonListener());
		
		JPanel panel = new JPanel(new GridLayout(1, 1));
		panel.setOpaque(false);
		panel.add(Box.createRigidArea(new Dimension(HSPACE, 0)));
		panel.add(button);
		panel.add(Box.createRigidArea(new Dimension(HSPACE, 0)));
		add(panel);
	}
	
	private double getTime()
	{
		return (System.currentTimeMillis() - gameStartTime) * 1000;
	}
	
	private class ButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			String com = e.getActionCommand();
			if (com.equals("create"))
			{
				parent.gotoCreateGame();
			}
			else if (com.equals("join"))
			{
				parent.gotoJoinGame();
			}
			else if (com.equals("editor"))
			{
				parent.gotoEditor();
			}
			else if (com.equals("credits"))
			{
				parent.gotoCredits();
			}
			else if (com.equals("exit"))
			{
				parent.exitGame();
			}
		}
	}
	
	private class ButtonIcon implements Icon
	{
		private JButton button;

		public ButtonIcon(JButton b)
		{
			button = b;
		}

		@Override
		public int getIconHeight()
		{
			return button.getHeight();
		}

		@Override
		public int getIconWidth()
		{
			return button.getWidth();
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			g.setColor(Color.gray);
			g.fillRect(0, 0, getIconWidth(), getIconHeight());
			
			Font f = new Font("Serif", Font.BOLD, 32);
			Point loc = WindowManager.centerText(g.getFontMetrics(f), button.getText(), getIconWidth(), getIconHeight());
			
			g.setColor(Color.green);
			g.setFont(f);
			g.drawString(button.getText(), loc.x, loc.y);
		}
	}
}
