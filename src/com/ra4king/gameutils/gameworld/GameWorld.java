package com.ra4king.gameutils.gameworld;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.util.ArrayList;

import com.ra4king.gameutils.Art;
import com.ra4king.gameutils.Entity;
import com.ra4king.gameutils.Game;
import com.ra4king.gameutils.Screen;
import com.ra4king.gameutils.util.Bag;

/**
 * A GameWorld is a container of Entities. It has a z-buffer that goes in back-to-front order, 0 being the back.
 * @author Roi Atalla
 */
public class GameWorld implements Screen {
	private Game parent;
	private ArrayList<Bag<Entity>> entities;
	private ArrayList<Temp> temp;
	private Image bg;
	private String bgImage;
	private double xOffset, yOffset;
	private boolean hasInited, hasShown;
	private volatile boolean isLooping;
	
	/**
	 * Initializes this object.
	 */
	public GameWorld() {
		entities = new ArrayList<Bag<Entity>>();
		entities.add(new Bag<Entity>());
		
		temp = new ArrayList<Temp>();
		
		setBackground(Color.lightGray);
	}
	
	public void init(Game game) {
		parent = game;
		
		//for(Entity e : getEntities())
		
		preLoop();
		
		try{
			for(Bag<Entity> b : entities)
				for(Entity e : b)
					if(e != null)
						e.init(this);
		}
		finally {
			postLoop();
		}
		
		hasInited = true;
	}
	
	/**
	 * Calls each Entity's <code>show()</code> method in z-index order.
	 */
	public synchronized void show() {
		hasShown = true;
		
		//for(Entity e : getEntities())
		
		preLoop();
		
		try{
			for(Bag<Entity> b : entities)
				for(Entity e : b)
					if(e != null)
						e.show();
		}
		finally {
			postLoop();
		}
	}
	
	/**
	 * Calls each Entity's <code>hide()</code> method in z-index order.
	 */
	public synchronized void hide() {
		hasShown = false;
		
		//for(Entity e : getEntities())
		
		preLoop();
		
		try{
			for(Bag<Entity> b : entities)
				for(Entity e : b)
					if(e != null)
						e.hide();
		}
		finally {
			postLoop();
		}
	}
	
	public synchronized void paused() {
		//for(Entity e : getEntities())
		
		preLoop();
		
		try{
			for(Bag<Entity> b : entities)
				for(Entity e : b)
					if(e != null)
						e.paused();
		}
		finally {
			postLoop();
		}
	}
	
	public synchronized void resumed() {
		//for(Entity e : getEntities())
		
		preLoop();
		
		try{
			for(Bag<Entity> b : entities)
				for(Entity e : b)
					if(e != null)
						e.resumed();
		}
		finally {
			postLoop();
		}
	}
	
	public synchronized void resized(int width, int height) {}
	
	/**
	 * Calls each Entity's <code>update(long)</code> method in z-index order.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public synchronized void update(long deltaTime) {
		//for(Entity e : getEntities())
		
		preLoop();
		
		try {
			for(Bag<Entity> b : entities)
				for(Entity e : b)
					if(e != null)
						try{
							e.update(deltaTime);
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
		}
		finally {
			postLoop();
		}
	}
	
	/**
	 * Draws the background then all the Entities in z-index order.
	 * @param g The Graphics context to draw to the screen.
	 */
	public synchronized void draw(Graphics2D g) {
		Image bg = (this.bg == null ? parent.getArt().get(bgImage) : this.bg);
		
		if(bg != null)
			g.drawImage(bg,0,0,getWidth(),getHeight(),0,0,bg.getWidth(null),bg.getHeight(null),null);
		
		//for(Entity e : getEntities())
		
		preLoop();
		
		g.translate(xOffset, yOffset);
		
		try{
			for(Bag<Entity> b : entities)
				for(Entity e : b)
					try{
						if(e != null)
							e.draw((Graphics2D)g.create());
					}
					catch(Exception exc) {
						exc.printStackTrace();
					}
		}
		finally {
			postLoop();
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
		return add(0,e);
	}
	
	/**
	 * Adds the Entity with the specified z-index.
	 * @param e The Entity to be added.
	 * @param zindex The z-index of this Entity.
	 * @return The Entity that was added.
	 */
	public synchronized Entity add(int zindex, Entity e) {
		if(isLooping) {
			temp.add(new Temp(zindex,e));
		}
		else {
			while(zindex >= entities.size())
				entities.add(new Bag<Entity>());
			
			entities.get(zindex).add(e);
			
			if(hasInited)
				e.init(this);
			
			if(hasShown)
				e.show();
		}
		
		return e;
	}
	
	/**
	 * Returns true if this GameWorld contains this Entity.
	 * @param e The Entity to search for.
	 * @return True if this GameWorld contains this Entity, false otherwise.
	 */
	public boolean contains(Entity e) {
		if(isLooping && temp.contains(e))
			return true;
		return getEntities().contains(e);
	}
	
	public boolean replace(Entity old, Entity e) {
		if(isLooping) {
			int i = temp.indexOf(old);
			if(i >= 0) {
				temp.get(i).e = e;
				return true;
			}
		}
		
		int zindex = getZIndex(old);
		if(zindex < 0)
			return false;
		
		boolean isNew = getZIndex(e) < 0;
		
		remove(e);
		
		Bag<Entity> bag = entities.get(zindex);
		bag.set(bag.indexOf(old),e);
		
		if(isNew) {
			e.init(this);
			e.show();
		}
		
		return true;
	}
	
	/**
	 * Removes the Entity from the world.
	 * @param e The Entity to remove.
	 * @return True if the Entity was found and removed, false if the Entity was not found.
	 */
	public synchronized boolean remove(Entity e) {
		boolean removed = false;
		
		for(Bag<Entity> bag : entities)
			removed |= bag.remove(e);
		
		if(removed)
			e.hide();
		
		return removed;
	}
	
	/**
	 * Clears this game world.
	 */
	public synchronized void clear() {
		entities.clear();
		temp.clear();
		
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
		if(isLooping) {
			int i = temp.indexOf(e);
			if(i >= 0) {
				temp.get(i).zIndex = newZIndex;
				return true;
			}
		}
		
		if(!remove(e))
			return false;
		
		add(newZIndex,e);
		
		e.show();
		
		return true;
	}
	
	/**
	 * Returns the z-index of the specified Entity.
	 * @param e The Entity who's index is returned.
	 * @return The z-index of the specified Entity, or -1 if the Entity was not found.
	 */
	public synchronized int getZIndex(Entity e) {
		if(isLooping) {
			int i = temp.indexOf(e);
			if(i >= 0)
				return temp.get(i).zIndex;
		}
		
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
	public synchronized ArrayList<Entity> getEntitiesAt(int zindex) {
		return entities.get(zindex);
	}
	
	/**
	 * A list of all Entities in this entire world.
	 * @return A list of all Entities in this world in z-index order.
	 */
	public synchronized ArrayList<Entity> getEntities() {
		ArrayList<Entity> allEntities = new ArrayList<Entity>();
		
		for(Bag<Entity> bag : entities)
			allEntities.addAll(bag);
		
		return allEntities;
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
		return getEntities().size();
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
	
	public void setXOffset(double xOffset) {
		this.xOffset = xOffset;
	}
	
	public double getXOffset() {
		return xOffset;
	}
	
	public void setYOffset(double yOffset) {
		this.yOffset = yOffset;
	}
	
	public double getYOffset() {
		return yOffset;
	}
	
	public void preLoop() {
		if(isLooping)
			return;
		
		temp.clear();
		isLooping = true;
	}
	
	public void postLoop() {
		if(!isLooping)
			return;
		
		isLooping = false;
		
		for(Temp p : temp)
			add(p.zIndex,p.e);
		
		temp.clear();
		
		for(Bag<Entity> bag : entities)
			bag.remove(null);
	}
	
	private class Temp {
		private Entity e;
		private int zIndex;
		
		Temp(int zIndex, Entity e) {
			this.zIndex = zIndex;
			this.e = e;
		}
	}
}