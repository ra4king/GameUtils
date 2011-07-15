package gameutils;

import java.awt.Graphics2D;

/**
 * An Element that can be added to a Screen.
 * @author Roi Atalla
 */
public interface Element {
	/**
	 * Called when this Element is added to a Screen.
	 * @param screen The parent Screen of this Element.
	 */
	public void init(Screen screen);
	
	/**
	 * Called when this Element is shown.
	 */
	public void show();
	
	/**
	 * Called when this Element is hidden.
	 */
	public void hide();
	
	/**
	 * Called when the parent Screen is paused.
	 */
	public void paused();
	
	/**
	 * Called when the parent Screen is resumed.
	 */
	public void resumed();
	
	/**
	 * Updates this Element.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public void update(long deltaTime);
	
	/**
	 * Draws this Element.
	 * @param g The Graphics context used to draw to the screen.
	 */
	public void draw(Graphics2D g);
}
