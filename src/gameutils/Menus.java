package gameutils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Menus organizes a group of MenuPages.
 * @author Roi Atalla
 */
public class Menus implements Screen {
	private Game game;
	private Map<String,MenuPage> menuPages;
	private MenuPage currentPage;
	private Image bg;
	private String bgImage;
	
	/**
	 * Initializes this object.
	 * @param game The parent of this object.
	 */
	public Menus() {
		menuPages = Collections.synchronizedMap(new HashMap<String,MenuPage>());
		
		setBackground(Color.lightGray);
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
	public MenuPage addPage(String name, MenuPage page) {
		if(name == null)
			throw new IllegalArgumentException("Name cannot be null");
		if(page == null)
			throw new IllegalArgumentException("MenuPage cannot be null");
		
		menuPages.put(name,page);
		
		game.addScreen(page, name);
		
		if(currentPage == null)
			currentPage = page;
		
		return page;
	}
	
	/**
	 * Returns the page with the specified description.
	 * @param description The description of the MenuPage.
	 * @return The MenuPage with the specified description, null if not found.
	 */
	public MenuPage getMenuPage(String name) {
		return menuPages.get(name.intern());
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
	 * Sets the current page displayed. Then calls 
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
	 * Sets the screen to the current MenuPage.
	 */
	public void show() {
		if(currentPage == null)
			return;
		
		game.setScreen(currentPage);
	}
	
	public void hide() {
		
	}
	
	/**
	 * Returns the current page displayed.
	 * @return The current page displayed.
	 */
	public MenuPage getMenuPageShown() {
		return currentPage;
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
	
	/**
	 * This calls the parent's getWidth() method.
	 * @return The width of this world.
	 */
	public int getWidth() {
		return game.getWidth();
	}
	
	/**
	 * This calls the parent's getHeight() method.
	 * @return The height of this world.
	 */
	public int getHeight() {
		return game.getHeight();
	}
	
	public void update(long deltaTime) {
		if(currentPage == null)
			return;
		
		currentPage.update(deltaTime);
	}
	
	/**
	 * Draws the background then current MenuPage displayed.
	 * @param g The Graphics context to draw to the screen.
	 */
	public void draw(Graphics2D g) {
		Graphics2D g2 = (Graphics2D)g.create();
		
		Image bg = (this.bg == null ? game.getArt().get(bgImage) : this.bg);
		
		if(bg != null)
			g2.drawImage(bg,0,0,getWidth(),getHeight(),0,0,bg.getWidth(null),bg.getHeight(null),null);
		
		if(currentPage != null)
			try{
				currentPage.draw(g2);
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
	}
}