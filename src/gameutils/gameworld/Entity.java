package gameutils.gameworld;

import gameutils.Element;
import gameutils.Screen;
import gameutils.util.FastMath;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

/**
 * All entities that are added to GameWorld must extend this class.
 * @author Roi Atalla
 */
public abstract class Entity implements Element {
	private GameWorld parent;
	private Rectangle2D.Double bounds;
	private Rectangle2D.Double areaBounds;
	private Area area;
	private double x, y, width, height;
	
	/**
	 * Sets the X, Y, width, and height to 0.
	 */
	public Entity() {
		this(0,0,0,0);
	}
	
	/**
	 * Sets the X, Y, width, and height.
	 * @param x The X position.
	 * @param y The Y position.
	 * @param w The width.
	 * @param h The height.
	 */
	public Entity(double x, double y, double w, double h) {
		bounds = new Rectangle2D.Double(x,y,w,h);
		
		this.x = x;
		this.y = y;
		width = w;
		height = h;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Entity) {
			Entity e = (Entity)o;
			return e.getBounds().equals(getBounds());
		}
		return false;
	}
	
	public void init(Screen world) {
		parent = (GameWorld)world;
	}
	
	/**
	 * @return The parent of this game component.
	 */
	public GameWorld getParent() {
		return parent;
	}
	
	/**
	 * @return The leftmost X position.
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * @return The nearest integer of the leftmost X position.
	 */
	public int getIntX() {
		return (int)FastMath.round(getX());
	}
	
	/**
	 * Sets the leftmost X position.
	 * @param x The new X value.
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	/**
	 * @return The topmost Y position.
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * @return The nearest integer of the topmost Y position.
	 */
	public int getIntY() {
		return (int)FastMath.round(getY());
	}
	
	/**
	 * Sets the topmost Y position.
	 * @param y The new Y value.
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * @return The width of this game component.
	 */
	public double getWidth() {
		return width;
	}
	
	public int getIntWidth() {
		return (int)FastMath.round(getWidth());
	}
	
	/**
	 * Sets the width of this game component
	 * @param width The new width.
	 */
	public void setWidth(double width) {
		this.width = width;
	}
	
	/**
	 * @return The height of this game component.
	 */
	public double getHeight() {
		return height;
	}
	
	public int getIntHeight() {
		return (int)FastMath.round(getHeight());
	}
	
	/**
	 * Sets the height of this game component.
	 * @param height The new height.
	 */
	public void setHeight(double height) {
		this.height = height;
	}
	
	/**
	 * Returns the center X position.
	 * @return The center X position.
	 */
	public double getCenterX() {
		return getX()+getWidth()/2;
	}
	
	/**
	 * Returns the nearest integer of the center X position.
	 * @return The nearest integer of the center X position.
	 */
	public int getIntCenterX() {
		return (int)FastMath.round(getCenterX());
	}
	
	/**
	 * Returns the center Y position.
	 * @return The center Y position.
	 */
	public double getCenterY() {
		return getY()+getHeight()/2;
	}
	
	/**
	 * Returns the nearest integer of the center Y position.
	 * @return The nearest integer of the center Y position.
	 */
	public int getIntCenterY() {
		return (int)FastMath.round(getCenterY());
	}
	
	/**
	 * @return The bounds of this game component.
	 */
	public Rectangle2D.Double getBounds() {
		bounds.setFrame(x,y,width,height);
		return bounds;
	}
	
	/**
	 * Returns the Area of this Entity. If one is not set, this Entity's bounds enclosed in an Area is returned.
	 * @return The Area of this Entity. If null, the bounds's Area is returned.
	 */
	public Area getArea() {
		return area == null ? new Area(getBounds()) : area;
	}
	
	/**
	 * Returns the bounds of the area. If one is not set, the normal bounds is returned.
	 * @return The bounds of the area. If one is not set, the normal bounds is returned.
	 */
	public Rectangle2D.Double getAreaBounds() {
		if(area == null)
			return bounds;
		
		areaBounds.setFrame(area.getBounds2D());
		return areaBounds;
	}
	
	/**
	 * Sets the Shape of this Entity.
	 * @param shape The new Shape of this Entity. The bounds of this Entity is updated with Rectangle2D returned from the Shape's getBounds2D() method.
	 */
	public void setArea(Area area) {
		Rectangle2D r = area.getBounds2D();
		bounds = new Rectangle2D.Double(r.getX(),r.getY(),r.getWidth(),r.getHeight());
		
		this.area = area;
		
		areaBounds = new Rectangle2D.Double();
		areaBounds.setFrame(area.getBounds2D());
	}
	
	public void show() {}
	
	public void hide() {}
	
	/**
	 * Called by the parent a set number of times a second.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public abstract void update(long deltaTime);
	
	/**
	 * Called by the parent a set number of times a second.
	 * @param g The Graphics context to draw to the screen.
	 */
	public abstract void draw(Graphics2D g);
}