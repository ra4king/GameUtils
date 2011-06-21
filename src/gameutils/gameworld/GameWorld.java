package gameutils.gameworld;

import gameutils.Art;
import gameutils.Game;
import gameutils.Screen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A GameWorld is a container of Entities. It has a z-buffer that goes in back-to-front order, 0 being the back.
 * @author Roi Atalla
 */
public class GameWorld implements Screen {
	private Game parent;
	private List<List<Entity>> entities;
	private List<Entity> allEntities;
	private Image bg;
	private String bgImage;
	
	/**
	 * Initializes this object.
	 * @param parent The parent of this object.
	 */
	public GameWorld() {
		entities = Collections.synchronizedList(new ArrayList<List<Entity>>());
		entities.add(Collections.synchronizedList(new ArrayList<Entity>()));
		
		allEntities = Collections.synchronizedList(new ArrayList<Entity>());
		
		setBackground(Color.lightGray);
	}
	
	public void init(Game game) {
		parent = game;
	}
	
	/**
	 * Calls each Entity's <code>show()</code> method in z-index order.
	 */
	public void show() {
		for(List<Entity> list : entities)
			for(Entity e : list)
				e.show();
	}
	
	/**
	 * Calls each Entity's <code>hide()</code> method in z-index order.
	 */
	public void hide() {
		for(List<Entity> list : entities)
			for(Entity e : list)
				e.hide();
	}
	
	/**
	 * Calls each Entity's <code>update(long)</code> method in z-index order.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public synchronized void update(long deltaTime) {
		for(List<Entity> list : entities)
			for(Entity comp : list)
				if(comp != null)
					comp.update(deltaTime);
		flush();
	}
	
	/**
	 * Draws the background then all the components in z-index order.
	 * @param g The Graphics context to draw to the screen.
	 */
	public synchronized void draw(Graphics2D g) {
		Image bg = (this.bg == null ? parent.getArt().get(bgImage) : this.bg);
		
		if(bg != null)
			g.drawImage(bg,0,0,getWidth(),getHeight(),0,0,bg.getWidth(null),bg.getHeight(null),null);
		
		for(List<Entity> list : entities) {
			for(Entity comp : list) {
				try{
					if(comp.getBounds().intersects(parent.getBounds()))
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
	public Game getParent() {
		return parent;
	}
	
	/**
	 * Adds the component with a z-index of 0.
	 * @param comp The Entity to be added.
	 * @return The Entity that was added.
	 */
	public synchronized Entity add(Entity e) {
		return add(e,0);
	}
	
	/**
	 * Adds the component with the specified z-index.
	 * @param comp The Entity to be added.
	 * @param zindex The z-index of this Entity.
	 * @return The Entity that was added.
	 */
	public synchronized Entity add(Entity e, int zindex) {
		while(zindex >= entities.size())
			entities.add(Collections.synchronizedList(new ArrayList<Entity>()));
		
		entities.get(zindex).add(e);
		
		e.init(this);
		
		return e;
	}
	
	public boolean contains(Entity e) {
		return getEntities().contains(e);
	}
	
	/**
	 * Removes the component from the world.
	 * @param comp The Entity to remove.
	 * @return True if the component was found and removed, false if the component was not found.
	 */
	public boolean remove(Entity e) {
		boolean removed = false;
		
		try{
			for(List<Entity> list : entities) {
				int index = list.indexOf(e);
				if(index >= 0) {
					list.set(index,null);
					removed = true;
				}
			}
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		
		return removed;
	}
	
	/**
	 * Clears this game world.
	 */
	public synchronized void clear() {
		entities.clear();
		
		System.gc();
		
		entities.add(Collections.synchronizedList(new ArrayList<Entity>()));
	}
	
	/**
	 * Returns the z-index of the specified component.
	 * @param comp The Entity who's index is returned.
	 * @return The z-index of the specified component, or -1 if the component was not found.
	 */
	public synchronized int getZIndex(Entity comp) {
		for(int a = 0; a < entities.size(); a++)
			if(entities.get(a).indexOf(comp) >= 0)
				return a;
		return -1;
	}
	
	/**
	 * A list of all components at the specified z-index.
	 * @param zindex The z-index.
	 * @return A list of all Entities at the specified z-index.
	 */
	public synchronized List<Entity> getEntities(int zindex) {
		return entities.get(zindex);
	}
	
	/**
	 * A list of all components in this entire world.
	 * @return A list of all Entities in this world in z-index order.
	 */
	public synchronized List<Entity> getEntities() {
		allEntities.clear();
		
		for(List<Entity> list : entities)
			allEntities.addAll(list);
		
		return allEntities;
	}
	
	private synchronized void flush() {
		for(List<Entity> list : entities)
			while(list.remove(null)) {}
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
	 * @return The total number of components in this world.
	 */
	public synchronized int size() {
		int size = 0;
		for(List<Entity> list : entities)
			size += list.size();
		return size;
	}
	
	/**
	 * This calls the parent's getCodeBase() method.
	 * @return The current working directory.
	 */
	public String getCodeBase() {
		return parent.getCodeBase().toString();
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
}