package gameutils.gameworld;

import gameutils.Entity;

public abstract class GameComponent extends Entity {
	public GameComponent() {
		this(0,0,0,0);
	}
	
	public GameComponent(double x, double y, double width, double height) {
		super(x,y,width,height);
	}
	
	public GameWorld getParent() {
		return (GameWorld)super.getParent();
	}
}
