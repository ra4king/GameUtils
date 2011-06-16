package gameutils.gui;

import gameutils.Element;
import gameutils.Screen;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * A MenuItem is an item which is added to a MenuPage.
 * @author Roi Atalla
 */
public abstract class Widget implements Element {
	private Screen parent;
	private Rectangle2D.Double bounds;
	
	public Widget() {
		this(0,0,0,0);
	}
	
	public Widget(double x, double y, double w, double h) {
		bounds = new Rectangle2D.Double(x,y,w,h);
	}
	
	public void init(Screen screen) {
		parent = screen;
	}
	
	public void show() {}
	
	public void hide() {}
	
	/**
	 * Updates this MenuItem.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public abstract void update(long deltaTime);
	
	/**
	 * Draws this MenuItem.
	 * @param g The Graphics context to draw to the screen.
	 */
	public abstract void draw(Graphics2D g);
	
	/**
	 * Returns the parent of this MenuItem.
	 * @return The parent of this MenuItem.
	 */
	public Screen getParent() {
		return parent;
	}
	
	/**
	 * Returns the X position.
	 * @return The X position.
	 */
	public double getX() {
		return bounds.x;
	}
	
	/**
	 * Returns the X position in integer precision.
	 * @return The X position in integer precision.
	 */
	public int getIntX() {
		return (int)Math.round(getX());
	}
	
	/**
	 * Sets the X position.
	 * @param x The new X position.
	 */
	public void setX(double x) {
		bounds.x = x;
	}
	
	/**
	 * Returns the Y position.
	 * @return The Y position.
	 */
	public double getY() {
		return bounds.y;
	}
	
	/**
	 * Returns the Y position in integer precision.
	 * @return The Y position in integer precision.
	 */
	public int getIntY() {
		return (int)Math.round(getY());
	}
	
	/**
	 * Sets the Y position.
	 * @param y The new Y position.
	 */
	public void setY(double y) {
		bounds.y = y;
	}
	
	/**
	 * Returns the width.
	 * @return The width.
	 */
	public double getWidth() {
		return bounds.width;
	}
	
	/**
	 * Returns the width in integer precision.
	 * @return The width in integer precision.
	 */
	public int getIntWidth() {
		return (int)Math.round(getWidth());
	}
	
	/**
	 * Sets the width.
	 * @param width The new width.
	 */
	public void setWidth(double width) {
		bounds.width = width;
	}
	
	/**
	 * Returns the height.
	 * @return The height.
	 */
	public double getHeight() {
		return bounds.height;
	}
	
	/**
	 * Returns the height in integer precision.
	 * @return The height in integer precision.
	 */
	public int getIntHeight() {
		return (int)Math.round(getHeight());
	}
	
	/**
	 * Sets the height.
	 * @param height The new height.
	 */
	public void setHeight(double height) {
		bounds.height = height;
	}
	
	/**
	 * Returns the bounds of this Widget.
	 * @return The bounds of this Widget.
	 */
	public Rectangle2D.Double getBounds() {
		return bounds;
	}
}