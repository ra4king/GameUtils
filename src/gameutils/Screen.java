package gameutils;

import java.awt.Graphics2D;

/**
 * A screen is what will be currently drawn in a game. Only one screen can be active at a time.
 * @author Roi A.
 */
public interface Screen {
	/**
	 * Called when this screen is added to a Game.
	 */
	public void init();
	
	/**
	 * Called when this screen is shown.
	 */
	public void show();
	
	/**
	 * Called when this screen is hidden.
	 */
	public void hide();
	
	/**
	 * Updates the screen.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public void update(long deltaTime);
	
	/**
	 * Draws this screen.
	 * @param g Object that used to draw to the screen.
	 */
	public void draw(Graphics2D g);
}