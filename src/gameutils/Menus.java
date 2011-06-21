package gameutils;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Menus organizes a group of MenuPages. <br>
 * <b>REMEMBER:</b> A Menus object is only temporarily set as the Screen in a Game. The show() method sets the Game's Screen as the current MenuPage.
 * This is important to consider when setting InputListeners. An InputListener should never be added on a Menus. 
 * @author Roi Atalla
 */
public class Menus implements Screen {
	private Game game;
	private Map<String,MenuPage> menuPages;
	private MenuPage currentPage;
	
	/**
	 * Initializes this object.
	 * @param game The parent of this object.
	 */
	public Menus() {
		menuPages = new HashMap<String,MenuPage>();
	}
	
	public void init(Game game) {
		this.game = game;
	}
	
	/**
	 * Returns the parent of this object.
	 * @return The parent of this object.
	 */
	public Game getParent() {
		return game;
	}
	
	/**
	 * Adds a page to the Menus. Neither name nor MenuPage can be null.
	 * @param page The MenuPage to add.
	 * @return The MenuPage that was added.
	 */
	public synchronized MenuPage addPage(String name, MenuPage page) {
		if(name == null)
			throw new IllegalArgumentException("Name cannot be null");
		if(page == null)
			throw new IllegalArgumentException("MenuPage cannot be null");
		
		menuPages.put(name,page);
		
		game.addScreen(page, name);
		
		return page;
	}
	
	/**
	 * Returns the page with the specified description.
	 * @param description The description of the MenuPage.
	 * @return The MenuPage with the specified description, null if not found.
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
	public synchronized String getMenuPageName(MenuPage page) {
		for(String s : menuPages.keySet())
			if(menuPages.get(s) == page)
				return s;
		return null;
	}
	
	/**
	 * Sets the current page displayed. This must be called after this Menus has been added and set to Game.
	 * @param pageName The description of the new page to display.
	 */
	public void setMenuPageShown(String name) {
		MenuPage page = getMenuPage(name);
		
		if(page == null)
			throw new IllegalArgumentException(name + " does not exist.");
		
		currentPage = page;
		
		game.setScreen(currentPage);
	}
	
	/**
	 * Sets the Game's Screen to the current MenuPage.
	 */
	public void show() {
		if(currentPage == null)
			return;
		
		game.setScreen(currentPage);
	}
	
	public void hide() {
		
	}
	
	//Shouldn't even be called.
	public void update(long deltaTime) {
		throw new RuntimeException("THIS METHOD SHOULDN'T BE CALLED!!!");
	}
	
	//Shouldn't even be called.
	public void draw(Graphics2D g) {
		throw new RuntimeException("THIS METHOD SHOULDN'T BE CALLED!!!");
	}
}