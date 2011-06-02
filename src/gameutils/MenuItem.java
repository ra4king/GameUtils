package gameutils;

import java.awt.Graphics2D;

/**
 * A MenuItem is an item which is added to a MenuPage.
 * @author Roi Atalla
 */
public abstract class MenuItem {
	private MenuPage parent;
	private String name;
	private int x, y, width, height;
	
	/**
	 * Initializes this object.
	 * @param name The name of this MenuItem.
	 */
	public MenuItem(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the parent of this MenuItem.
	 * @return The parent of this MenuItem.
	 */
	public MenuPage getParent() {
		return parent;
	}
	
	/**
	 * Sets the parent of this MenuItem.
	 * @param parent The new parent of this MenuItem.
	 */
	public void setParent(MenuPage parent) {
		if(parent == null)
			throw new NullPointerException("parent is null!");
		
		this.parent = parent;
	}
	
	/**
	 * Returns the name of this MenuItem.
	 * @return The name of this MenuItem.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of this MenuItem.
	 * @param name The new name of this MenuItem.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the X position.
	 * @return The X position.
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Sets the X position.
	 * @param x The new X position.
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * Returns the Y position.
	 * @return The Y position.
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Sets the Y position.
	 * @param y The new Y position.
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * Returns the width.
	 * @return The width.
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Sets the width.
	 * @param width The new width.
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	/**
	 * Returns the height.
	 * @return The height.
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Sets the height.
	 * @param height The new height.
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	/**
	 * Notifies this MenuItem that it has been activated/deactivated.
	 * @param isActive If true, this MenuItem is actived, else it is deactivated.
	 */
	protected void setActive(boolean isActive) {
	}
	
	/**
	 * Draws this MenuItem.
	 * @param g The Graphics context to draw to the screen.
	 */
	public abstract void draw(Graphics2D g);
}