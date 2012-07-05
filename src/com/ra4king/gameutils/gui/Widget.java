package com.ra4king.gameutils.gui;


import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.ra4king.gameutils.Entity;
import com.ra4king.gameutils.InputListener;
import com.ra4king.gameutils.Screen;

/**
 * A Widget could be added to any Screen.
 * @author Roi Atalla
 */
public abstract class Widget extends Entity {
	private InputListener input;
	
	private boolean hasFocus, isFocusable = true;
	
	/**
	 * Sets X, Y, width and height to 0.
	 */
	public Widget() {}
	
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
	
	@Override
	public void init(Screen screen) {
		super.init(screen);
		
		input = new InputListener() {
			@Override
			public void keyPressed(KeyEvent key, Screen screen) {
				if(!isFocusable || hasFocus)
					Widget.this.keyPressed(key);
			}
			
			@Override
			public void keyReleased(KeyEvent key, Screen screen) {
				if(!isFocusable || hasFocus)
					Widget.this.keyReleased(key);
			}
			
			@Override
			public void keyTyped(KeyEvent key, Screen screen) {
				if(!isFocusable || hasFocus)
					Widget.this.keyTyped(key);
			}
			
			@Override
			public void mouseEntered(MouseEvent me, Screen screen) {
				if(!isFocusable || getBounds().contains(me.getPoint()))
					Widget.this.mouseEntered(me);
			}
			
			@Override
			public void mouseExited(MouseEvent me, Screen screen) {
				if(!isFocusable || getBounds().contains(me.getPoint()))
					Widget.this.mouseExited(me);
			}
			
			@Override
			public void mousePressed(MouseEvent me, Screen screen) {
				if(!isFocusable || getBounds().contains(me.getPoint())) {
					if(!hasFocus)
						gainFocus();
					
					Widget.this.mousePressed(me);
				}
				else if(hasFocus)
					loseFocus();
			}
			
			@Override
			public void mouseReleased(MouseEvent me, Screen screen) {
				if(!isFocusable || hasFocus)
					Widget.this.mouseReleased(me);
			}
			
			@Override
			public void mouseClicked(MouseEvent me, Screen screen) {
				if(!isFocusable || getBounds().contains(me.getPoint()))
					Widget.this.mouseClicked(me);
			}
			
			@Override
			public void mouseDragged(MouseEvent me, Screen screen) {
				if(!isFocusable || hasFocus)
					Widget.this.mouseDragged(me);
			}
			
			@Override
			public void mouseMoved(MouseEvent me, Screen screen) {
				if(!isFocusable || getBounds().contains(me.getPoint()))
					Widget.this.mouseMoved(me);
			}
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent mwe, Screen screen) {
				if(!isFocusable || getBounds().contains(mwe.getPoint()))
					Widget.this.mouseWheelMoved(mwe);
			}
		};
	}
	
	public boolean isFocusable() {
		return isFocusable;
	}
	
	public void setFocusable(boolean isFocusable) {
		this.isFocusable = isFocusable;
	}
	
	@Override
	public void show() {
		getParent().getGame().addInputListener(getParent(),input);
	}
	
	@Override
	public void hide() {
		getParent().getGame().removeInputListener(getParent(),input);
	}
	
	@Override
	public void update(long deltaTime) {}
	
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
