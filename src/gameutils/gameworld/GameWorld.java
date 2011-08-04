package gameutils.gameworld;

import gameutils.Art;
import gameutils.Element;
import gameutils.Game;
import gameutils.Screen;
import gameutils.util.Bag;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.util.ArrayList;

/**
 * A GameWorld is a container of Entities. It has a z-buffer that goes in back-to-front order, 0 being the back.
 * @author Roi Atalla
 */
public class GameWorld implements Screen {
	private Game parent;
	private ArrayList<Bag<Element>> entities;
	private ArrayList<Element> allEntities;
	private Image bg;
	private String bgImage;
	private boolean hasShown;
	private boolean renderOutOfBoundsEntities;
	
	/**
	 * Initializes this object.
	 */
	public GameWorld() {
		entities = new ArrayList<Bag<Element>>();
		entities.add(new Bag<Element>());
		
		allEntities = new ArrayList<Element>();
		
		setBackground(Color.lightGray);
		
		renderOutOfBoundsEntities = true;
	}
	
	public void init(Game game) {
		parent = game;
	}
	
	/**
	 * Calls each Element's <code>show()</code> method in z-index order.
	 */
	public synchronized void show() {
		hasShown = true;
		
		for(Bag<Element> bag : entities)
			for(Element e : bag)
				e.show();
	}
	
	/**
	 * Calls each Element's <code>hide()</code> method in z-index order.
	 */
	public synchronized void hide() {
		hasShown = false;
		
		for(Bag<Element> bag : entities)
			for(Element e : bag)
				e.hide();
	}
	
	public synchronized void paused() {
		for(Bag<Element> bag : entities)
			for(Element e: bag)
				e.paused();
	}
	
	public synchronized void resumed() {
		for(Bag<Element> bag : entities)
			for(Element e : bag)
				e.resumed();
	}
	
	public synchronized void resized(int width, int height) {}
	
	/**
	 * Calls each Element's <code>update(long)</code> method in z-index order.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public synchronized void update(long deltaTime) {
		Element em = null;
		try {
			for(Element e : getElements()) {
				em = e;
				if(e != null)
					e.update(deltaTime);
			}
		}
		catch(RuntimeException exc) {
			System.out.println(em);
			throw exc;
		}
		flush();
	}
	
	/**
	 * Draws the background then all the Entities in z-index order.
	 * @param g The Graphics context to draw to the screen.
	 */
	public synchronized void draw(Graphics2D g) {
		Image bg = (this.bg == null ? parent.getArt().get(bgImage) : this.bg);
		
		if(bg != null)
			g.drawImage(bg,0,0,getWidth(),getHeight(),0,0,bg.getWidth(null),bg.getHeight(null),null);
		
		for(Element e : getElements()) {
			try{
				if(e != null && (renderOutOfBoundsEntities || e.getBounds().intersects(parent.getBounds())))
					e.draw((Graphics2D)g.create());
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
		}
		
		flush();
	}
	
	/**
	 * @return The parent of this object.
	 */
	public Game getGame() {
		return parent;
	}
	
	/**
	 * Adds the Element with a z-index of 0.
	 * @param e The Element to be added.
	 * @return The Element that was added.
	 */
	public synchronized Element add(Element e) {
		return add(e,0);
	}
	
	/**
	 * Adds the Element with the specified z-index.
	 * @param e The Element to be added.
	 * @param zindex The z-index of this Element.
	 * @return The Element that was added.
	 */
	public synchronized Element add(Element e, int zindex) {
		while(zindex >= entities.size())
			entities.add(new Bag<Element>());
		
		entities.get(zindex).add(e);
		
		e.init(this);
		
		if(hasShown)
			e.show();
		
		return e;
	}
	
	/**
	 * Returns true if this GameWorld contains this Element.
	 * @param e The Element to search for.
	 * @return True if this GameWorld contains this Element, false otherwise.
	 */
	public boolean contains(Element e) {
		return getElements().contains(e);
	}
	
	public boolean replace(Element old, Element e) {
		int zindex = getZIndex(old);
		if(zindex < 0)
			return false;
		
		if(getZIndex(e) < 0)
			return false;
		
		remove(e);
		
		Bag<Element> bag = entities.get(zindex);
		bag.set(bag.indexOf(old),e);
		return true;
	}
	
	/**
	 * Removes the Element from the world.
	 * @param e The Element to remove.
	 * @return True if the Element was found and removed, false if the Element was not found.
	 */
	public synchronized boolean remove(Element e) {
		for(Bag<Element> bag : entities) {
			if(bag.remove(e)) {
				e.hide();
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Clears this game world.
	 */
	public synchronized void clear() {
		entities.clear();
		
		System.gc();
		
		entities.add(new Bag<Element>());
	}
	
	/**
	 * Changes the z-index of the specified Element.
	 * @param e The Element whose z-index is changed.
	 * @param newZIndex The new z-index
	 * @return True if the Element was found and updated, false otherwise.
	 */
	public synchronized boolean changeZIndex(Element e, int newZIndex) {
		if(remove(e))
			return false;
		
		add(e,newZIndex);
		
		e.show();
		
		return true;
	}
	
	/**
	 * Returns the z-index of the specified Element.
	 * @param e The Element who's index is returned.
	 * @return The z-index of the specified Element, or -1 if the Element was not found.
	 */
	public synchronized int getZIndex(Element e) {
		for(int a = 0; a < entities.size(); a++)
			if(entities.get(a).indexOf(e) >= 0)
				return a;
		return -1;
	}
	
	/**
	 * Returns true if the specified z-index exists.
	 * @param zindex The z-index to check.
	 * @return True if the specified z-index exists, false otherwise.
	 */
	public synchronized boolean containsZIndex(int zindex) {
		return zindex < entities.size();
	}
	
	/**
	 * A list of all Entities at the specified z-index.
	 * @param zindex The z-index.
	 * @return A list of all Entities at the specified z-index.
	 */
	public synchronized Bag<Element> getElementsAt(int zindex) {
		return entities.get(zindex);
	}
	
	/**
	 * A list of all Entities in this entire world.
	 * @return A list of all Entities in this world in z-index order.
	 */
	public synchronized ArrayList<Element> getElements() {
		allEntities.clear();
		
		for(Bag<Element> bag : entities)
			allEntities.addAll(bag);
		
		return allEntities;
	}
	
	private synchronized void flush() {
		for(Bag<Element> bag : entities)
			while(bag.remove(null)) {}
	}
	
	/**
	 * Sets the background of this GameWorld with an image in Art.
	 * @param s The associated name of an image in Art. This image will be drawn before all other Entities.
	 */
	public synchronized void setBackground(String s) {
		bg = null;
		bgImage = s;
	}
	
	/**
	 * Sets the background of this GameWorld. A compatible image is created.
	 * @param bg The image to be drawn before all other Entities.
	 */
	public synchronized void setBackground(Image bg) {
		bgImage = null;
		
		this.bg = Art.createCompatibleImage(bg);
	}
	
	/**
	 * Sets the background to the specified color.
	 * This method creates a 1x1 image with the specified color
	 * and stretches it to the width and height of the parent.
	 * @param color The color to be used as the entire background. It will be drawn before all other Entities.
	 */
	public synchronized void setBackground(Color color) {
		bgImage = null;
		
		bg = Art.createCompatibleImage(1, 1, color.getAlpha() == 0 || color.getAlpha() == 255 ? (color.getAlpha() == 0 ? Transparency.BITMASK : Transparency.OPAQUE) : Transparency.TRANSLUCENT);
		Graphics g = bg.getGraphics();
		g.setColor(color);
		g.fillRect(0,0,1,1);
		g.dispose();
	}
	
	/**
	 * Returns the background image.
	 * @return The image used as the background.
	 */
	public Image getBackgroundImage() {
		if(bg == null)
			return parent.getArt().get(bgImage);
		return bg;
	}
	
	/**
	 * @return The total number of Entities in this world.
	 */
	public synchronized int size() {
		int size = 0;
		for(Bag<Element> bag : entities)
			size += bag.size();
		return size;
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
	
	/**
	 * Sets whether to render entities whose bounds are outside the screen. Default is true.
	 * @param render If true, all entities are rendered, else only those whose bounds are inside the screen.
	 */
	public void setRenderOutOfBoundsEntities(boolean render) {
		renderOutOfBoundsEntities = render;
	}
	
	/**
	 * Returns whether to render entities whose bounds are outside the screen. Default is true.
	 * @return True if all entities are rendered. false if only those whose bounds are inside the screen.
	 */
	public boolean isRenderingOutOfBoundsEntities() {
		return renderOutOfBoundsEntities;
	}
}