package gameutils.gui;

import gameutils.Entity;
import gameutils.InputListener;
import gameutils.Screen;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * A Widget could be added to any Screen.
 * @author Roi Atalla
 */
public abstract class Widget extends Entity {
	private boolean hasFocus;
	
	/**
	 * Sets X, Y, width and height to 0.
	 */
	public Widget() {
		super();
	}
	
	/**
	 * Sets the X, Y, width, and height.
	 * @param x The X position.
	 * @param y The Y position.
	 * @param w The width.
	 * @param h The height.
	 */
	public Widget(double x, double y, double w, double h) {
		super(x,y,w,h);
	}
	
	public void init(Screen screen) {
		super.init(screen);
		
		screen.getGame().addInputListener(screen, new InputListener() {
			public void keyPressed(KeyEvent key, Screen screen) {
				if(hasFocus)
					Widget.this.keyPressed(key);
			}
			
			public void keyReleased(KeyEvent key, Screen screen) {
				if(hasFocus)
					Widget.this.keyReleased(key);
			}
			
			public void keyTyped(KeyEvent key, Screen screen) {
				if(hasFocus)
					Widget.this.keyTyped(key);
			}
			
			public void mouseEntered(MouseEvent me, Screen screen) {
				if(getBounds().contains(me.getPoint()))
					Widget.this.mouseEntered(me);
			}
			
			public void mouseExited(MouseEvent me, Screen screen) {
				if(getBounds().contains(me.getPoint()))
					Widget.this.mouseExited(me);
			}
			
			public void mousePressed(MouseEvent me, Screen screen) {
				if(getBounds().contains(me.getPoint())) {
					if(!hasFocus)
						gainFocus();
					
					Widget.this.mousePressed(me);
				}
				else if(hasFocus)
					loseFocus();
			}
			
			public void mouseReleased(MouseEvent me, Screen screen) {
				if(getBounds().contains(me.getPoint()))
					Widget.this.mouseReleased(me);
			}
			
			public void mouseClicked(MouseEvent me, Screen screen) {
				if(getBounds().contains(me.getPoint()))
					Widget.this.mouseClicked(me);
			}
			
			public void mouseDragged(MouseEvent me, Screen screen) {
				if(getBounds().contains(me.getPoint()))
					Widget.this.mouseDragged(me);
			}
			
			public void mouseMoved(MouseEvent me, Screen screen) {
				if(getBounds().contains(me.getPoint()))
					Widget.this.mouseMoved(me);
			}
			
			public void mouseWheelMoved(MouseWheelEvent mwe, Screen screen) {
				if(getBounds().contains(mwe.getPoint()))
					Widget.this.mouseWheelMoved(mwe);
			}
		});
	}
	
	public void gainFocus() {
		this.hasFocus = true;
		focusGained();
	}
	
	public void loseFocus() {
		this.hasFocus = false;
		focusLost();
	}
	
	public boolean hasFocus() {
		return hasFocus;
	}
	
	public void keyPressed(KeyEvent key) {}
	
	public void keyReleased(KeyEvent key) {}
	
	public void keyTyped(KeyEvent key) {}
	
	public void mouseEntered(MouseEvent me) {}
	
	public void mouseExited(MouseEvent me) {}
	
	public void mousePressed(MouseEvent me) {}
	
	public void mouseReleased(MouseEvent me) {}
	
	public void mouseClicked(MouseEvent me) {}
	
	public void mouseDragged(MouseEvent me) {}
	
	public void mouseMoved(MouseEvent me) {}
	
	public void mouseWheelMoved(MouseWheelEvent mwe) {}
	
	public void focusGained() {}
	
	public void focusLost() {}
}
