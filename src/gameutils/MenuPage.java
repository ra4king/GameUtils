package gameutils;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * A MenuPage is a page in a set of Menus.
 * @author Roi Atalla
 */
public class MenuPage {
	private Menus parent;
	private ArrayList<MenuButton> buttons;
	private ArrayList<MenuItem> items;
	private String name;
	
	/**
	 * Initializes this object.
	 * @param name The name of this MenuPage.
	 */
	public MenuPage(String name) {
		buttons = new ArrayList<MenuButton>();
		items = new ArrayList<MenuItem>();
		this.name = name;
	}
	
	/**
	 * Returns the parent of this MenuPage.
	 * @return The parent of this MenuPage.
	 */
	public Menus getParent() {
		return parent;
	}
	
	/**
	 * Sets the parent of this MenuPage.
	 * @param parent The parent of this MenuPage.
	 */
	public void setParent(Menus parent) {
		this.parent = parent;
	}
	
	/**
	 * Adds a MenuItem to this MenuPage.
	 * @param item The MenuItem to add.
	 * @return The MenuItem added.
	 */
	public synchronized MenuItem addItem(MenuItem item) {
		if(item != null) {
			
			if(item instanceof MenuButton)
				buttons.add((MenuButton)item);
			
			items.add(item);
			
			item.setParent(this);
		}
		
		return item;
	}
	
	/**
	 * Returns the MenuItem at the specified index.
	 * @param idx The index of the MenuItem.
	 * @return The MenuItem at the specified index.
	 */
	public MenuItem getItem(int idx) {
		return items.get(idx);
	}
	
	/**
	 * Returns the MenuItem with the specified name.
	 * @param name The name of the MenuItem.
	 * @return The MenuItem with the specified name.
	 */
	public MenuItem getItem(String name) {
		for(MenuItem i : items)
			if(i.getName().equals(name))
				return i;
		
		return null;
	}
	
	/**
	 * Returns the name of this MenuPage.
	 * @return The name of this MenuPage.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of this MenuPage.
	 * @param name The new name of this MenuPage.
	 */
	public void setName(String name) {
		if(name == null)
			throw new NullPointerException("name is null!");
		
		this.name = name;
	}
	
	/**
	 * Calls the parent's getWidth() method.
	 * @return The width of this MenuPage.
	 */
	public int getWidth() {
		return parent.getWidth();
	}
	
	/**
	 * Calls the parent's getHeight() method.
	 * @return The height of this MenuPage.
	 */
	public int getHeight() {
		return parent.getHeight();
	}
	
	/**
	 * Calls the mouseReleased method of all MenuItems on this MenuPage.
	 * @param x The X position of the mouse release.
	 * @param y The Y position of the mouse release.
	 */
	public void mouseReleased(int x, int y) {
		for(MenuButton b : buttons) {
			b.setHighlighted(false);
			
			if(b.getBounds().contains(x,y) && b.isEnabled())
				if(b.isPressed())
					b.getAction().doAction(b);
				else
					b.setHighlighted(true);
			
			b.setPressed(false);
		}
	}
	
	/**
	 * Calls the mousePressed of all MenuItems on this MenuPage.
	 * @param x The X position of the mouse press.
	 * @param y The Y position of the mouse press.
	 */
	public void mousePressed(int x, int y) {
		for(MenuButton b : buttons) {
			b.setPressed(false);
			b.setHighlighted(false);
			
			if(b.getBounds().contains(x,y) && b.isEnabled())
				b.setPressed(true);
		}
	}
	
	/**
	 * Calls the mouseMoved of all MenuItems on this MenuPage.
	 * @param x The X position of the mouse move.
	 * @param y The Y position of the mouse move.
	 */
	public void mouseMoved(int x, int y) {
		for(MenuButton b : buttons) {
			b.setHighlighted(false);
			
			if(b.getBounds().contains(x,y) && b.isEnabled() && !b.isPressed())
				b.setHighlighted(true);
		}
	}
	
	/**
	 * Notifies all MenuItems on this MenuPage of activation/deactivation.
	 * @param isActive If true, this MenuPage is active, else this MenuPage is deactivated.
	 */
	protected void setActive(boolean isActive) {
		for(MenuItem i : items)
			i.setActive(isActive);
	}
	
	/**
	 * Draws all MenuItems on this MenuPage.
	 * @param g The Graphics context to draw to the screen.
	 */
	public synchronized void draw(Graphics2D g) {
		for(MenuItem i : items)
			i.draw(g);
	}
}