package gameutils.gameworld;

import gameutils.Entity;

public abstract class GameComponent extends Entity {
	public GameWorld getParent() {
		return (GameWorld)super.getParent();
	}
}
