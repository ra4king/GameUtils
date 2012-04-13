package com.ra4king.gameutils;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.util.ArrayList;

import com.ra4king.gameutils.gui.Widget;

/**
 * A MenuPage is a Screen that holds and organizes a set of Widgets.
 * @author Roi Atalla
 */
public class MenuPage implements Screen {
	private Menus menus;
	private Game game;
	private ArrayList<Widget> widgets;
	private Image bg;
	private String bgImage;
	private boolean hasInited;
	
	/**
	 * Initializes this object.
	 * @param menus The Menus parent of this MenuPage.
	 */
	public MenuPage(Menus menus) {
		if(menus == null)
			throw new IllegalArgumentException("Menus cannot be null.");
		
		this.menus = menus;
		
		widgets = new ArrayList<Widget>();
		
		setBackground(Color.lightGray);
	}
	
	public void init(Game game) {
		this.game = game;
		
		hasInited = true;
		
		for(Widget w : widgets)
			w.init(this);
	}
	
	/**
	 * Returns the parent of this MenuPage.
	 * @return The parent of this MenuPage.
	 */
	public Game getGame() {
		return game;
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
	
	public void paused() {
		for(Widget w : widgets)
			w.paused();
	}
	
	public void resumed() {
		for(Widget w : widgets)
			w.resumed();
	}
	
	public void resized(int width, int height) {}
	
	/**
	 * Calls all added Widget's update(long) method.
	 */
	public void update(long deltaTime) {
		for(Widget w : widgets)
			w.update(deltaTime);
	}
	
	/**
	 * Draws the background then calls all added Widget's draw(Graphics2D) method in the order they were added in.
	 */
	public void draw(Graphics2D g) {
		Image bg = (this.bg == null ? game.getArt().get(bgImage) : this.bg);
		
		if(bg != null)
			g.drawImage(bg,0,0,getWidth(),getHeight(),0,0,bg.getWidth(null),bg.getHeight(null),null);
		
		for(Widget w : widgets)
			w.draw((Graphics2D)g.create());
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
		
		if(hasInited)
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
		return game.getWidth();
	}
	
	/**
	 * Calls the parent's getHeight() method.
	 * @return The height of this MenuPage.
	 */
	public int getHeight() {
		return game.getHeight();
	}
	
	/**
	 * Sets the background of this component with an image in Art.
	 * @param s The associated name of an image in Art. This image will be drawn before all other components.
	 */
	public void setBackground(String s) {
		bg = null;
		bgImage = s;
	}
	
	/**
	 * Sets the background of this component. A compatible image is created.
	 * @param bg The image to be drawn before all other components.
	 */
	public void setBackground(Image bg) {
		bgImage = null;
		
		this.bg = Art.createCompatibleImage(bg);
	}
	
	/**
	 * Sets the background to the specified color.
	 * This method creates a 1x1 image with the specified color
	 * and stretches it to the width and height of the parent.
	 * @param color The color to be used as the entire background. It will be drawn before all other components.
	 */
	public void setBackground(Color color) {
		bgImage = null;
		
		bg = Art.createCompatibleImage(1, 1, color.getAlpha() == 0 || color.getAlpha() == 255 ? (color.getAlpha() == 0 ? Transparency.BITMASK : Transparency.OPAQUE) : Transparency.TRANSLUCENT);
		Graphics g = bg.getGraphics();
		g.setColor(color);
		g.fillRect(0,0,1,1);
	}
	
	/**
	 * Returns the background image.
	 * @return The image used as the background.
	 */
	public Image getBackgroundImage() {
		return bg;
	}
}