package com.ra4king.gameutils.gameworld;

import com.ra4king.gameutils.Entity;
import com.ra4king.gameutils.Screen;

public abstract class GameComponent extends Entity {
	public GameComponent() {
		this(0,0,0,0);
	}
	
	public GameComponent(double x, double y, double width, double height) {
		super(x,y,width,height);
	}
	
	public final void init(Screen screen) {
		super.init(screen);
		init((GameWorld)screen);
	}
	
	public void init(GameWorld gameWorld) {}
	
	public GameWorld getParent() {
		return (GameWorld)super.getParent();
	}
}
