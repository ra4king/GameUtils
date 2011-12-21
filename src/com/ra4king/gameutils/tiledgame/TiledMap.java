package com.ra4king.gameutils.tiledgame;


import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.ra4king.gameutils.BasicScreen;
import com.ra4king.gameutils.Game;

public class TiledMap extends BasicScreen {
	private CellEntity[][] map;
	private Camera camera;
	private boolean hasInited, hasShown;
	
	public final int CELL_WIDTH, CELL_HEIGHT;
	
	public TiledMap(int xCells, int yCells, int cellWidth, int cellHeight) {
		map = new CellEntity[xCells][yCells];
		camera = new Camera(xCells,yCells);
		
		CELL_WIDTH = cellWidth;
		CELL_HEIGHT = cellHeight;
	}
	
	public void init(Game game) {
		super.init(game);
		
		hasInited = true;
		
		for(CellEntity[] ea : map)
			for(CellEntity e : ea)
				if(e != null)
					e.init(this);
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public CellEntity set(int x, int y, CellEntity e) {
		map[x][y] = e;
		
		if(hasInited)
			e.init(this);
		if(hasShown)
			e.show();
		
		e.setLocation(x, y);
		
		return e;
	}
	
	public CellEntity get(int x, int y) {
		return map[x][y];
	}
	
	public void move(int x, int y, int newX, int newY) {
		set(newX,newY,remove(x,y));
	}
	
	public CellEntity remove(int x, int y) {
		CellEntity e = map[x][y];
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
	
	public void centerCamera(CellEntity ce) {
		camera.xOffset = -ce.getScreenX()+getWidth()/2-ce.getWidth()/2;
		camera.yOffset = -ce.getScreenY()+getHeight()/2-ce.getHeight()/2;
	}
	
	public void show() {
		hasShown = true;
		
		for(CellEntity[] ea : map)
			for(CellEntity e : ea)
				if(e != null)
					e.show();
	}
	
	public void hide() {
		for(CellEntity[] ea : map)
			for(CellEntity e : ea)
				if(e != null)
					e.hide();
	}
	
	public void paused() {
		for(CellEntity[] ea : map)
			for(CellEntity e : ea)
				if(e != null)
					e.paused();
	}
	
	public void resumed() {
		for(CellEntity[] ea : map)
			for(CellEntity e : ea)
				if(e != null)
					e.resumed();
	}
	
	public void update(long deltaTime) {
		for(CellEntity[] ea : map)
			for(CellEntity e : ea)
				if(e != null)
					e.update(deltaTime);
	}
	
	public void draw(Graphics2D g) {
		AffineTransform old = g.getTransform();
		
		AffineTransform at = new AffineTransform();
		at.translate(camera.xOffset, camera.yOffset);
		g.setTransform(at);
		
		for(CellEntity[] ea : map)
			for(CellEntity e : ea)
				if(e != null)
					e.draw((Graphics2D)g.create());
		
		g.setTransform(old);
	}
}
