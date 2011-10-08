package gameutils.tiledgame;

import java.awt.Graphics2D;

import gameutils.Entity;

public class CellEntity extends Entity {
	public CellEntity() {
		this(0,0,0,0);
	}
	
	public CellEntity(int x, int y, int width, int height) {
		super(x,y,width,height);
	}
	
	public TiledMap getParent() {
		return (TiledMap)super.getParent();
	}
	
	public void update(long deltaTime) {}
	
	public void draw(Graphics2D g) {}
}
