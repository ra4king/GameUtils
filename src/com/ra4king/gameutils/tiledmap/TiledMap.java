package com.ra4king.gameutils.tiledmap;


import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.ra4king.gameutils.BasicScreen;
import com.ra4king.gameutils.Game;

public class TiledMap extends BasicScreen {
	private Cell[][] map;
	private Camera camera;
	private boolean hasInited, hasShown;
	
	public final int CELL_WIDTH, CELL_HEIGHT;
	
	public TiledMap(int xCells, int yCells, int cellWidth, int cellHeight) {
		map = new Cell[xCells][yCells];
		
		CELL_WIDTH = cellWidth;
		CELL_HEIGHT = cellHeight;
	}
	
	public void init(Game game) {
		super.init(game);
		
		camera = new Camera(getWidth(),getHeight());
		
		hasInited = true;
		
		for(Cell[] ea : map)
			for(Cell e : ea)
				if(e != null)
					e.init(this);
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public Cell set(int x, int y, Cell e) {
		map[x][y] = e;
		
		if(hasInited)
			e.init(this);
		if(hasShown)
			e.show();
		
		e.setLocation(x, y);
		
		return e;
	}
	
	public Cell get(int x, int y) {
		return map[x][y];
	}
	
	public void move(int x, int y, int newX, int newY) {
		set(newX,newY,remove(x,y));
	}
	
	public Cell remove(int x, int y) {
		Cell e = map[x][y];
		map[x][y] = null;
		return e;
	}
	
	public int getWidth() {
		return getGame().getWidth();
	}
	
	public int getHeight() {
		return getGame().getHeight();
	}
	
	public int getCellXNum() {
		return map.length;
	}
	
	public int getCellYNum() {
		try{
			return map[0].length;
		}
		catch(ArrayIndexOutOfBoundsException exc) {
			return 0;
		}
	}
	
	public void centerCamera(Cell ce) {
		camera.centerAt(ce.getScreenX()+ce.getWidth()/2,ce.getScreenY()+ce.getHeight()/2);
	}
	
	public void show() {
		hasShown = true;
		
		for(Cell[] ea : map)
			for(Cell e : ea)
				if(e != null)
					e.show();
	}
	
	public void hide() {
		for(Cell[] ea : map)
			for(Cell e : ea)
				if(e != null)
					e.hide();
	}
	
	public void paused() {
		for(Cell[] ea : map)
			for(Cell e : ea)
				if(e != null)
					e.paused();
	}
	
	public void resumed() {
		for(Cell[] ea : map)
			for(Cell e : ea)
				if(e != null)
					e.resumed();
	}
	
	public void update(long deltaTime) {
		for(Cell[] ea : map)
			for(Cell e : ea)
				if(e != null)
					e.update(deltaTime);
	}
	
	public void draw(Graphics2D g) {
		AffineTransform old = g.getTransform();
		
		AffineTransform at = new AffineTransform();
		at.translate(camera.xOffset, camera.yOffset);
		g.setTransform(at);
		
		for(Cell[] ea : map)
			for(Cell e : ea)
				if(e != null)
					e.draw((Graphics2D)g.create());
		
		g.setTransform(old);
	}
}
