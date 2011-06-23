package gameutils.gui;

import gameutils.Element;
import gameutils.Screen;
import gameutils.util.FastMath;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * A Widget could be added to any Screen.
 * @author Roi Atalla
 */
public abstract class Widget implements Element {
	private Screen parent;
	private Rectangle2D.Double bounds;
	private double x, y, width, height;
	
	/**
	 * Sets X, Y, width and height to 0.
	 */
	public Widget() {
		this(0,0,0,0);
	}
	
	/**
	 * Sets the X, Y, width, and height.
	 * @param x The X position.
	 * @param y The Y position.
	 * @param w The width.
	 * @param h The height.
	 */
	public Widget(double x, double y, double w, double h) {
		bounds = new Rectangle2D.Double(x,y,w,h);
		
		this.x = x;
		this.y = y;
		width = w;
		height = h;
	}
	
	public void init(Screen screen) {
		parent = screen;
	}
	
	public void show() {}
	
	public void hide() {}
	
	/**
	 * Updates this Widget.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public abstract void update(long deltaTime);
	
	/**
	 * Draws this Widget.
	 * @param g The Graphics context to draw to the screen.
	 */
	public abstract void draw(Graphics2D g);
	
	/**
	 * Returns the parent of this Widget.
	 * @return The parent of this Widget.
	 */
	public Screen getParent() {
		return parent;
	}
	
	/**
	 * Returns the X position.
	 * @return The X position.
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Returns the X position in integer precision.
	 * @return The X position in integer precision.
	 */
	public int getIntX() {
		return (int)FastMath.round(getX());
	}
	
	/**
	 * Sets the X position.
	 * @param x The new X position.
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	/**
	 * Returns the Y position.
	 * @return The Y position.
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * Returns the Y position in integer precision.
	 * @return The Y position in integer precision.
	 */
	public int getIntY() {
		return (int)FastMath.round(getY());
	}
	
	/**
	 * Sets the Y position.
	 * @param y The new Y position.
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * Returns the width.
	 * @return The width.
	 */
	public double getWidth() {
		return width;
	}
	
	/**
	 * Returns the width in integer precision.
	 * @return The width in integer precision.
	 */
	public int getIntWidth() {
		return (int)FastMath.round(getWidth());
	}
	
	/**
	 * Sets the width.
	 * @param width The new width.
	 */
	public void setWidth(double width) {
		this.width = width;
	}
	
	/**
	 * Returns the height.
	 * @return The height.
	 */
	public double getHeight() {
		return height;
	}
	
	/**
	 * Returns the height in integer precision.
	 * @return The height in integer precision.
	 */
	public int getIntHeight() {
		return (int)FastMath.round(getHeight());
	}
	
	/**
	 * Sets the height.
	 * @param height The new height.
	 */
	public void setHeight(double height) {
		this.height = height;
	}
	
	/**
	 * Returns the bounds of this Widget.
	 * @return The bounds of this Widget.
	 */
	public Rectangle2D.Double getBounds() {
		bounds.setFrame(x,y,width,height);
		return bounds;
	}
}