package gameutils.gameworld;

import gameutils.Art;
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
	private ArrayList<Bag<Entity>> entities;
	private ArrayList<Entity> allEntities;
	private Image bg;
	private String bgImage;
	private boolean hasShown;
	private boolean renderOutOfBoundsEntities;
	
	/**
	 * Initializes this object.
	 */
	public GameWorld() {
		entities = new ArrayList<Bag<Entity>>();
		entities.add(new Bag<Entity>());
		
		allEntities = new ArrayList<Entity>();
		
		setBackground(Color.lightGray);
		
		renderOutOfBoundsEntities = true;
	}
	
	public void init(Game game) {
		parent = game;
	}
	
	/**
	 * Calls each Entity's <code>show()</code> method in z-index order.
	 */
	public synchronized void show() {
		hasShown = true;
		
		for(Bag<Entity> bag : entities)
			for(Entity e : bag)
				e.show();
	}
	
	/**
	 * Calls each Entity's <code>hide()</code> method in z-index order.
	 */
	public synchronized void hide() {
		hasShown = false;
		
		for(Bag<Entity> bag : entities)
			for(Entity e : bag)
				e.hide();
	}
	
	public synchronized void paused() {
		for(Bag<Entity> bag : entities)
			for(Entity e: bag)
				e.paused();
	}
	
	public synchronized void resumed() {
		for(Bag<Entity> bag : entities)
			for(Entity e : bag)
				e.resumed();
	}
	
	public synchronized void resized(int width, int height) {}
	
	/**
	 * Calls each Entity's <code>update(long)</code> method in z-index order.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public synchronized void update(long deltaTime) {
		for(Bag<Entity> bag : entities)
			for(Entity comp : bag)
				if(comp != null)
					comp.update(deltaTime);
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
		
		for(Bag<Entity> bag : entities) {
			for(Entity comp : bag) {
				try{
					if(renderOutOfBoundsEntities || comp.getBounds().intersects(parent.getBounds()))
						comp.draw((Graphics2D)g.create());
				}
				catch(Exception exc) {
					exc.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * @return The parent of this object.
	 */
	public Game getGame() {
		return parent;
	}
	
	/**
	 * Adds the Entity with a z-index of 0.
	 * @param e The Entity to be added.
	 * @return The Entity that was added.
	 */
	public synchronized Entity add(Entity e) {
		return add(e,0);
	}
	
	/**
	 * Adds the Entity with the specified z-index.
	 * @param e The Entity to be added.
	 * @param zindex The z-index of this Entity.
	 * @return The Entity that was added.
	 */
	public synchronized Entity add(Entity e, int zindex) {
		while(zindex >= entities.size())
			entities.add(new Bag<Entity>());
		
		entities.get(zindex).add(e);
		
		e.init(this);
		
		if(hasShown)
			e.show();
		
		return e;
	}
	
	/**
	 * Returns true if this GameWorld contains this Entity.
	 * @param e The Entity to search for.
	 * @return True if this GameWorld contains this Entity, false otherwise.
	 */
	public boolean contains(Entity e) {
		return getEntities().contains(e);
	}
	
	public boolean replace(Entity old, Entity e) {
		int zindex = getZIndex(old);
		if(zindex < 0)
			return false;
		
		if(getZIndex(e) < 0)
			return false;
		
		remove(e);
		
		Bag<Entity> bag = entities.get(zindex);
		bag.set(bag.indexOf(old),e);
		return true;
	}
	
	/**
	 * Removes the Entity from the world.
	 * @param e The Entity to remove.
	 * @return True if the Entity was found and removed, false if the Entity was not found.
	 */
	public boolean remove(Entity e) {
		for(Bag<Entity> bag : entities) {
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
		
		entities.add(new Bag<Entity>());
	}
	
	/**
	 * Changes the z-index of the specified Entity.
	 * @param e The Entity whose z-index is changed.
	 * @param newZIndex The new z-index
	 * @return True if the Entity was found and updated, false otherwise.
	 */
	public synchronized boolean changeZIndex(Entity e, int newZIndex) {
		if(remove(e))
			return false;
		
		add(e,newZIndex);
		
		e.show();
		
		return true;
	}
	
	/**
	 * Returns the z-index of the specified Entity.
	 * @param e The Entity who's index is returned.
	 * @return The z-index of the specified Entity, or -1 if the Entity was not found.
	 */
	public synchronized int getZIndex(Entity e) {
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
	public synchronized Bag<Entity> getEntities(int zindex) {
		return entities.get(zindex);
	}
	
	/**
	 * A list of all Entities in this entire world.
	 * @return A list of all Entities in this world in z-index order.
	 */
	public synchronized ArrayList<Entity> getEntities() {
		allEntities.clear();
		
		for(Bag<Entity> bag : entities)
			allEntities.addAll(bag);
		
		return allEntities;
	}
	
	private synchronized void flush() {
		for(Bag<Entity> bag : entities)
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
		for(Bag<Entity> bag : entities)
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