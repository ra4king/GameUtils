package gameutils.tiledgame;

import gameutils.BasicScreen;
import gameutils.Game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

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
		
		e.setBounds(x*CELL_WIDTH, y*CELL_HEIGHT,CELL_WIDTH,CELL_HEIGHT);
		
		return e;
	}
	
	public CellEntity get(int x, int y) {
		return map[x][y];
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
		at.translate(camera.xOffset*CELL_WIDTH, camera.yOffset*CELL_HEIGHT);
		g.setTransform(at);
		
		for(CellEntity[] ea : map)
			for(CellEntity e : ea)
				if(e != null)
					e.draw((Graphics2D)g.create());
		
		g.setTransform(old);
		
		g.setColor(Color.black);
		g.drawString(camera.xOffset + " " + camera.yOffset,200,getHeight()-2);
	}
}
