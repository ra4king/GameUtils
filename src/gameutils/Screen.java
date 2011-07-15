package gameutils;

import java.awt.Graphics2D;

/**
 * A Screen is a game state. Only one Screen can be active in a Game at a time.
 * @author Roi A.
 */
public interface Screen {
	/**
	 * Called when this Screen is added to a Game.
	 * @param game The parent Game of this Screen.
	 */
	public void init(Game game);
	
	/**
	 * Returns the parent of this Screen.
	 * @return The parent of this Screen.
	 */
	public Game getParent();
	
	/**
	 * Called when this Screen is shown.
	 */
	public void show();
	
	/**
	 * Called when this Screen is hidden.
	 */
	public void hide();
	
	/**
	 * Called when this Screen is paused.
	 */
	public void paused();
	
	/**
	 * Called when this Screen is resumed.
	 */
	public void resumed();
	
	/**
	 * Called when this Screen is resized.
	 */
	public void resized(int width, int height);
	
	/**
	 * Updates this Screen.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public void update(long deltaTime);
	
	/**
	 * Draws this Screen.
	 * @param g The Graphics context to draw to the screen.
	 */
	public void draw(Graphics2D g);
}