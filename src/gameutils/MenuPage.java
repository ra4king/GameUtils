package gameutils;

import gameutils.gui.Widget;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * A MenuPage is a page in a set of Menus.
 * @author Roi Atalla
 */
public class MenuPage implements Element {
	private Menus parent;
	private ArrayList<Widget> widgets;
	
	/**
	 * Initializes this object.
	 * @param name The name of this MenuPage.
	 */
	public MenuPage() {
		widgets = new ArrayList<Widget>();
	}
	
	public void init(Screen screen) {
		this.parent = (Menus)screen;
	}
	
	/**
	 * Calls all added MenuItem's show() method.
	 */
	public void show() {
		for(Widget mi : widgets)
			mi.show();
	}
	
	/**
	 * Calls all added MenuItem's hide() method.
	 */
	public void hide() {
		for(Widget mi : widgets)
			mi.hide();
	}
	
	public void update(long deltaTime) {
		for(Widget i : widgets)
			i.update(deltaTime);
	}
	
	public void draw(Graphics2D g) {
		for(Widget i : widgets)
			i.draw(g);
	}
	
	/**
	 * Returns the parent of this MenuPage.
	 * @return The parent of this MenuPage.
	 */
	public Menus getParent() {
		return parent;
	}
	
	/**
	 * Adds a MenuItem to this MenuPage.
	 * @param widget The MenuItem to add.
	 * @return The MenuItem added.
	 */
	public Widget add(Widget widget) {
		if(widget == null)
			throw new IllegalArgumentException("Widget cannot be null.");
		
		widgets.add(widget);
		
		widget.init(parent);
		
		return widget;
	}
	
	/**
	 * Returns the MenuItem at the specified index.
	 * @param idx The index of the MenuItem.
	 * @return The MenuItem at the specified index.
	 */
	public Widget getWidget(int idx) {
		return widgets.get(idx);
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
}