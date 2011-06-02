package gameutils;

import gameutils.networking.Packet;
import gameutils.networking.Serializable;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * All entities that are added to GameWorld must extend this class.
 * @author Roi Atalla
 *
 */
public abstract class GameComponent implements Serializable {
	private GameWorld parent;
	private Rectangle2D.Double bounds;
	private double x, y;
	private int width, height;
	
	/**
	 * Sets the parent of this game component.
	 * @param parent The GameWorld parent of this game component.
	 */
	protected void setParent(GameWorld parent) {
		this.parent = parent;
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
		return (int)Math.round(x);
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
		return (int)Math.round(y);
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
	public int getWidth() {
		return width;
	}
	
	/**
	 * Sets the width of this game component
	 * @param width The new width.
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	/**
	 * @return The height of this game component.
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Sets the height of this game component.
	 * @param height The new height.
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	/**
	 * @return The bounds of this game component.
	 */
	public Rectangle2D.Double getBounds() {
		if(bounds == null)
			bounds = new Rectangle2D.Double();
		
		bounds.setFrame(x,y,width,height);
		return bounds;
	}
	
	/**
	 * Writes this object's data into the Packet.
	 */
	public void serialize(Packet p) {
		p.writeDouble(x);
		p.writeDouble(y);
		p.writeInt(width);
		p.writeInt(height);
	}
	
	/**
	 * Constructs this object from the Packet.
	 */
	public void deserialize(Packet p) {
		x = p.readDouble();
		y = p.readDouble();
		width = p.readInt();
		height = p.readInt();
	}
	
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