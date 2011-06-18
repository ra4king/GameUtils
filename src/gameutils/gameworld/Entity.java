package gameutils.gameworld;

import gameutils.Element;
import gameutils.Screen;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

/**
 * TODO: FIX THIS ENTIRE THING: shape.getBounds2D() isn't persistant!
 * All entities that are added to GameWorld must extend this class.
 * @author Roi Atalla
 */
public abstract class Entity implements Element {
	private GameWorld parent;
	private Rectangle2D.Double bounds;
	private Rectangle2D.Double areaBounds;
	private Area area;
	
	public Entity() {
		this(0,0,0,0);
	}
	
	public Entity(double x, double y, double w, double h) {
		bounds = new Rectangle2D.Double(x,y,w,h);
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
		return bounds.x;
	}
	
	/**
	 * @return The nearest integer of the leftmost X position.
	 */
	public int getIntX() {
		return (int)Math.round(getX());
	}
	
	/**
	 * Sets the leftmost X position.
	 * @param x The new X value.
	 */
	public void setX(double x) {
		bounds.x = x;
	}
	
	/**
	 * @return The topmost Y position.
	 */
	public double getY() {
		return bounds.y;
	}
	
	/**
	 * @return The nearest integer of the topmost Y position.
	 */
	public int getIntY() {
		return (int)Math.round(getY());
	}
	
	/**
	 * Sets the topmost Y position.
	 * @param y The new Y value.
	 */
	public void setY(double y) {
		getBounds().setRect(getX(),y,getWidth(),getHeight());
	}
	
	/**
	 * @return The width of this game component.
	 */
	public double getWidth() {
		return getBounds().getWidth();
	}
	
	public int getIntWidth() {
		return (int)Math.round(getWidth());
	}
	
	/**
	 * Sets the width of this game component
	 * @param width The new width.
	 */
	public void setWidth(double width) {
		getBounds().setRect(getX(),getY(),width,getHeight());
	}
	
	/**
	 * @return The height of this game component.
	 */
	public double getHeight() {
		return getBounds().getHeight();
	}
	
	public double getIntHeight() {
		return (int)Math.round(getHeight());
	}
	
	/**
	 * Sets the height of this game component.
	 * @param height The new height.
	 */
	public void setHeight(double height) {
		getBounds().setRect(getX(),getY(),getWidth(),height);
	}
	
	/**
	 * @return The bounds of this game component.
	 */
	public Rectangle2D.Double getBounds() {
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