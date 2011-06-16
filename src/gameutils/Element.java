package gameutils;

import java.awt.Graphics2D;

/**
 * An Element can be added to a class that implements TODO:Make up class name.
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
