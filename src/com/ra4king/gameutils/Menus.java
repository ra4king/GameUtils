package com.ra4king.gameutils;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Menus organizes a group of MenuPages. <br>
 * <b>REMEMBER:</b> A Menus object is only temporarily set as the Screen in a Game. The show() method sets the Game's Screen as the current MenuPage.
 * This is important to consider when setting InputListeners. <b>Adding an InputListener to a Menus is pointless and should not be done. Instead, add it to a specific MenuPage.</b> 
 * @author Roi Atalla
 */
public class Menus implements Screen {
	private Game game;
	private Map<String,MenuPage> menuPages;
	private MenuPage currentPage;
	private boolean hasInited;
	
	/**
	 * Initializes this object.
	 * @param game The parent of this object.
	 */
	public Menus() {
		menuPages = new HashMap<String,MenuPage>();
	}
	
	@Override
	public void init(Game game) {
		this.game = game;
		
		hasInited = true;
		
		for(String s : menuPages.keySet())
			game.addScreen(s,menuPages.get(s));
		
		if(currentPage != null)
			game.setScreen(currentPage);
	}
	
	/**
	 * Returns the parent of this object.
	 * @return The parent of this object.
	 */
	@Override
	public Game getGame() {
		return game;
	}
	
	/**
	 * Adds a page to the Menus. Neither name nor MenuPage can be null.
	 * @param name The name of the MenuPage.
	 * @param page The MenuPage to add.
	 * @return The MenuPage that was added.
	 */
	public MenuPage addPage(String name, MenuPage page) {
		if(name == null)
			throw new IllegalArgumentException("Name cannot be null");
		if(page == null)
			throw new IllegalArgumentException("MenuPage cannot be null");
		
		menuPages.put(name,page);
		
		if(currentPage == null)
			currentPage = page;
		
		if(hasInited)
			game.addScreen(name,page);
		
		return page;
	}
	
	/**
	 * Returns the page with the specified description.
	 * @param name The name of the MenuPage.
	 * @return The MenuPage with the specified name, null if not found.
	 */
	public MenuPage getMenuPage(String name) {
		return menuPages.get(name);
	}
	
	/**
	 * Returns the current page displayed.
	 * @return The current page displayed.
	 */
	public MenuPage getMenuPageShown() {
		return currentPage;
	}
	
	/**
	 * Returns the name of the MenuPage.
	 * @param page The MenuPage whose name is returned.
	 * @return The name of the MenuPage. If it is not found, returns null.
	 */
	public String getMenuPageName(MenuPage page) {
		for(String s : menuPages.keySet())
			if(menuPages.get(s) == page)
				return s;
		return null;
	}
	
	/**
	 * Sets the current page displayed. This must be called after this Menus has been added and set to Game.
	 * @param name The name of the new page to display.
	 */
	public void setMenuPageShown(String name) {
		MenuPage page = getMenuPage(name);
		
		if(page == null)
			throw new IllegalArgumentException(name + " does not exist.");
		
		currentPage = page;
		
		game.setScreen(currentPage);
	}
	
	public void setMenuPageShown(MenuPage page) {
		if(!menuPages.containsValue(page))
			throw new IllegalArgumentException("MenuPage hasn't been added to this Menus.");
		
		currentPage = page;
		
		game.setScreen(currentPage);
	}
	
	/**
	 * Sets the Game's Screen to the current MenuPage.
	 */
	@Override
	public void show() {
		if(currentPage != null)
			game.setScreen(currentPage);
	}
	
	@Override
	public void hide() {}
	
	@Override
	public void paused() {}
	
	@Override
	public void resumed() {}
	
	@Override
	public void resized(int width, int height) {}
	
	/**
	 * Throws an exception.
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void update(long deltaTime) {
		throw new UnsupportedOperationException("THIS METHOD SHOULDN'T BE CALLED!!!");
	}
	
	/**
	 * Throws an exception.
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void draw(Graphics2D g) {
		throw new UnsupportedOperationException("THIS METHOD SHOULDN'T BE CALLED!!!");
	}
}