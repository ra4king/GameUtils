package gameutils;

import gameutils.gui.Widget;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * A MenuPage is a page in a set of Menus. It holds and organizes a set of Widgets.
 * @author Roi Atalla
 */
public class MenuPage implements Screen {
	private Menus menus;
	private Game game;
	private ArrayList<Widget> widgets;
	
	/**
	 * Initializes this object.
	 * @param name The name of this MenuPage.
	 */
	public MenuPage(Menus menus) {
		if(menus == null)
			throw new IllegalArgumentException("Menus cannot be null.");
		
		this.menus = menus;
		
		widgets = new ArrayList<Widget>();
	}
	
	public void init(Game game) {
		this.game = game;
	}
	
	/**
	 * Calls all added Widget's show() method.
	 */
	public void show() {
		for(Widget w : widgets)
			w.show();
	}
	
	/**
	 * Calls all added Widget's hide() method.
	 */
	public void hide() {
		for(Widget w : widgets)
			w.hide();
	}
	
	/**
	 * Calls all added Widget's update(long) method.
	 */
	public void update(long deltaTime) {
		for(Widget w : widgets)
			w.update(deltaTime);
	}
	
	/**
	 * Draws all added Widget's draw(Graphics2D) method in the order they were added in.
	 */
	public void draw(Graphics2D g) {
		for(Widget w : widgets)
			w.draw(g);
	}
	
	/**
	 * Returns the parent of this MenuPage.
	 * @return The parent of this MenuPage.
	 */
	public Game getParent() {
		return game;
	}
	
	/**
	 * Returns the Menus this MenuPage is added to.
	 * @return The Menus this MenuPage is added to.
	 */
	public Menus getMenus() {
		return menus;
	}
	
	/**
	 * Adds a Widget to this MenuPage.
	 * @param widget The Widget to add.
	 * @return The Widget added.
	 */
	public Widget add(Widget widget) {
		if(widget == null)
			throw new IllegalArgumentException("Widget cannot be null.");
		
		widgets.add(widget);
		
		widget.init(this);
		
		return widget;
	}
	
	/**
	 * Returns the Widget at the specified index.
	 * @param idx The index of the Widget.
	 * @return The Widget at the specified index.
	 */
	public Widget getWidget(int idx) {
		return widgets.get(idx);
	}
	
	/**
	 * Removes the Widget from this MenuPage.
	 * @param widget The Widget to be removed.
	 * @return True if the Widget has been found and removed, false otherwise.
	 */
	public boolean remove(Widget widget) {
		return widgets.remove(widget);
	}
	
	/**
	 * Calls the parent's getWidth() method.
	 * @return The width of this MenuPage.
	 */
	public int getWidth() {
		return menus.getWidth();
	}
	
	/**
	 * Calls the parent's getHeight() method.
	 * @return The height of this MenuPage.
	 */
	public int getHeight() {
		return menus.getHeight();
	}
}