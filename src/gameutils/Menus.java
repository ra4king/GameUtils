package gameutils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Menus is a container of MenuPages.
 * @author Roi Atalla
 */
public class Menus implements Screen {
	private Game parent;
	private ArrayList<MenuPage> menuPages;
	private Image bg;
	private String bgImage;
	private int pageShown = -1;
	
	/**
	 * Initializes this object.
	 * @param parent The parent of this object.
	 */
	public Menus(Game parent) {
		this.parent = parent;
		
		menuPages = new ArrayList<MenuPage>();
		
		setBackground(Color.lightGray);
		
		MenuListener ml = new MenuListener();
		parent.addInputListener(this,ml);
	}
	
	/**
	 * Initializes this object and adds the specified pages.
	 * @param parent The parent of this object.
	 * @param pages The MenuPages to add. The first one is the one shown.
	 */
	public Menus(Game parent, MenuPage ... pages) {
		this(parent);
		
		for(MenuPage p : pages) {
			pageShown = 0;
			menuPages.add(p);
		}
	}
	
	/**
	 * Returns the parent of this object.
	 * @return The parent of this object.
	 */
	public Game getParent() {
		return parent;
	}
	
	/**
	 * Adds a page to the Menus.
	 * @param page The MenuPage to add.
	 * @return The MenuPage that was added.
	 */
	public synchronized MenuPage addPage(MenuPage page) {
		if(page != null) {
			if(pageShown == -1)
				pageShown = 0;
			
			menuPages.add(page);
			
			page.setParent(this);
			
			if(menuPages.size() == 1)
				page.setActive(true);
		}
		
		return page;
	}
	
	/**
	 * Returns the page at the specified index.
	 * @param idx The index of the MenuPage.
	 * @return The MenuPage at the specified index.
	 */
	public MenuPage getPage(int idx) {
		return menuPages.get(idx);
	}
	
	/**
	 * Returns the page with the specified description.
	 * @param description The description of the MenuPage.
	 * @return The MenuPage with the specified description, null if not found.
	 */
	public MenuPage getPage(String description) {
		for(MenuPage p : menuPages)
			if(p.getName().equals(description))
				return p;
		
		return null;
	}
	
	/**
	 * Returns the index of the specified page.
	 * @param page The MenuPage who's index is returned.
	 * @return The index of the specified MenuPage, -1 if not found.
	 */
	public int getPageIndex(MenuPage page) {
		return menuPages.indexOf(page);
	}
	
	/**
	 * Sets the current page displayed.
	 * @param pageNum The index of the new page to display.
	 */
	public synchronized void setPageShown(int pageNum) {
		if(pageNum < 0 || pageNum >= menuPages.size())
			throw new IllegalArgumentException("pageNum is out of bounds.");
		
		if(pageShown != -1)
			menuPages.get(pageShown).setActive(false);
		menuPages.get(pageNum).setActive(true);
		
		pageShown = pageNum;
	}
	
	/**
	 * Sets the current page displayed. Then calls 
	 * @param pageName The description of the new page to display.
	 */
	public synchronized void setPageShown(String pageName) {
		for(int a = 0; a < menuPages.size(); a++)
			if(menuPages.get(a).getName().equals(pageName))
				setPageShown(a);
	}
	
	public void show() {
		if(pageShown == -1)
			return;
		
		menuPages.get(pageShown).setActive(true);
	}
	
	public void hide() {
		if(pageShown == -1)
			return;
		
		menuPages.get(pageShown).setActive(false);
	}
	
	/**
	 * Returns the index of the current page displayed.
	 * @return The index of the current page displayed.
	 */
	public int getPageShownIndex() {
		return pageShown;
	}
	
	/**
	 * Returns the current page displayed.
	 * @return The current page displayed.
	 */
	public MenuPage getPageShown() {
		return menuPages.get(pageShown);
	}
	
	/**
	 * Sets the background of this component with an image in Art.
	 * @param s The associated name of an image in Art. This image will be drawn before all other components.
	 */
	public synchronized void setBackground(String s) {
		bg = null;
		bgImage = s;
	}
	
	/**
	 * Sets the background of this component. A compatible image is created.
	 * @param bg The image to be drawn before all other components.
	 */
	public synchronized void setBackground(Image bg) {
		bgImage = null;
		
		this.bg = Art.createCompatibleImage(bg);
	}
	
	/**
	 * Sets the background to the specified color.
	 * This method creates a 1x1 image with the specified color
	 * and stretches it to the width and height of the parent.
	 * @param color The color to be used as the entire background. It will be drawn before all other components.
	 */
	public synchronized void setBackground(Color color) {
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
		return parent.getWidth();
	}
	
	/**
	 * This calls the parent's getHeight() method.
	 * @return The height of this world.
	 */
	public int getHeight() {
		return parent.getHeight();
	}
	
	public void update(long deltaTime) {
		
	}
	
	/**
	 * Draws the background then current MenuPage displayed.
	 * @param g The Graphics context to draw to the screen.
	 */
	public void draw(Graphics2D g) {
		Graphics2D g2 = (Graphics2D)g.create();
		
		Image bg = (this.bg == null ? parent.getArt().get(bgImage) : this.bg);
		
		if(bg != null)
			g2.drawImage(bg,0,0,getWidth(),getHeight(),0,0,bg.getWidth(null),bg.getHeight(null),null);
		
		if(pageShown != -1)
			try{
				menuPages.get(pageShown).draw(g2);
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
	}
	
	private class MenuListener extends InputListener {
		public void mouseReleased(MouseEvent me) {
			if(me.getButton() == MouseEvent.BUTTON1 && pageShown != -1)
				menuPages.get(pageShown).mouseReleased(me.getX(),me.getY());
		}
		
		public void mousePressed(MouseEvent me) {
			if(me.getButton() == MouseEvent.BUTTON1 && pageShown != -1)
				menuPages.get(pageShown).mousePressed(me.getX(),me.getY());
		}
		
		public void mouseMoved(MouseEvent me) {
			if(pageShown != -1)
				menuPages.get(pageShown).mouseMoved(me.getX(),me.getY());
		}
	}
	
	/**
	 * This interface is in use mainly but the MenuButton class.
	 * @author Roi Atalla
	 */
	public static interface Action {
		/**
		 * Called when an action has occurred.
		 * @param button The MenuButton where an action has occurred.
		 */
		public void doAction(MenuButton button);
	}
}