package com.ra4king.gameutils.tiledmap;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.ra4king.gameutils.Element;
import com.ra4king.gameutils.Screen;

public abstract class Cell implements Element {
	private TiledMap parent;
	private int x, y;
	private Rectangle bounds;
	
	public Cell() {
		bounds = new Rectangle();
	}
	
	@Override
	public void init(Screen parent) {
		this.parent = (TiledMap)parent;
		if(getWidth() == 0)
			setWidth(this.parent.CELL_WIDTH);
		if(getHeight() == 0)
			setHeight(this.parent.CELL_HEIGHT);
	}
	
	@Override
	public TiledMap getParent() {
		return parent;
	}
	
	public int getCellX() {
		return x;
	}
	
	public int getScreenX() {
		return x*parent.CELL_WIDTH-(getWidth()-parent.CELL_WIDTH);
	}
	
	public void setCellX(int x) {
		if(this.x == x)
			return;
		
		if(parent != null && parent.get(this.x, y) == this)
			parent.remove(this.x, y);
		
		this.x = x;
		
		if(parent != null && parent.get(this.x, y) != this)
			parent.set(this.x, y, this);
	}
	
	public int getCellY() {
		return y;
	}
	
	public int getScreenY() {
		return y*parent.CELL_HEIGHT-(getHeight()-parent.CELL_HEIGHT);
	}
	
	public void setCellY(int y) {
		if(this.y == y)
			return;
		
		if(parent != null && parent.get(x, this.y) == this)
			parent.remove(x, this.y);
		
		this.y = y;
		
		if(parent != null && parent.get(x, this.y) != this)
			parent.set(x, this.y, this);
	}
	
	public int getWidth() {
		return bounds.width;
	}
	
	public void setWidth(int width) {
		bounds.width = width;
	}
	
	public int getHeight() {
		return bounds.height;
	}
	
	public void setHeight(int height) {
		bounds.height = height;
	}
	
	public void setLocation(int x, int y) {
		setCellX(x);
		setCellY(y);
	}
	
	public void setSize(int width, int height) {
		setWidth(width);
		setHeight(height);
	}
	
	public Rectangle getBounds() {
		bounds.setLocation(x*parent.CELL_WIDTH, y*parent.CELL_HEIGHT);
		return bounds;
	}
	
	public void setBounds(int x, int y, int width, int height) {
		setCellX(x);
		setCellY(y);
		setWidth(width);
		setHeight(height);
	}
	
	@Override
	public void show() {}
	
	@Override
	public void hide() {}
	
	@Override
	public void paused() {}
	
	@Override
	public void resumed() {}
	
	@Override
	public abstract void update(long deltaTime);
	
	@Override
	public abstract void draw(Graphics2D g);
}
