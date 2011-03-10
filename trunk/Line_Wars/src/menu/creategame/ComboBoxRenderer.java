package menu.creategame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class ComboBoxRenderer extends BasicComboBoxRenderer {

	private static final long serialVersionUID = 5860676115203832231L;
	
	private Color color = Color.black;
    private boolean selected = false;

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        setBackground((Color) value);
        color = (Color) value;
        selected = isSelected;
        setPreferredSize(new Dimension(40, 28));
        return this;
    }

    @Override
    public void paint(Graphics g) {
       super.paint(g);
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (selected) {
            g.setColor(Color.black);
            ((Graphics2D) g).setStroke(new BasicStroke(2));
            g.drawRect(1, 1, getWidth()-2, getHeight()-2);
        }
    }
}
