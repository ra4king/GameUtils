package gameutils;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Takes care of most abstract methods.
 * @author Roi Atalla
 */
public abstract class BasicScreen implements Screen, InputListener {
	private Game game;
	
	/**
	 * Stores the reference to [code]game[/code] and adds itself as an InputListener.
	 */
	public void init(Game game) {
		this.game = game;
		game.addInputListener(this, this);
	}
	
	public Game getParent() {
		return game;
	}
	
	public void show() {}
	
	public void hide() {}
	
	public void paused() {}
	
	public void resumed() {}
	
	public void resized(int width, int height) {}
	
	public abstract void update(long deltaTime);
	
	public abstract void draw(Graphics2D g);
	
	public void keyPressed(KeyEvent key, Screen screen) {}
	
	public void keyReleased(KeyEvent key, Screen screen) {}
	
	public void keyTyped(KeyEvent key, Screen screen) {}
	
	public void mouseEntered(MouseEvent me, Screen screen) {}
	
	public void mouseExited(MouseEvent me, Screen screen) {}
	
	public void mousePressed(MouseEvent me, Screen screen) {}
	
	public void mouseReleased(MouseEvent me, Screen screen) {}
	
	public void mouseClicked(MouseEvent me, Screen screen) {}
	
	public void mouseDragged(MouseEvent me, Screen screen) {}
	
	public void mouseMoved(MouseEvent me, Screen screen) {}
	
	public void mouseWheelMoved(MouseWheelEvent mwe, Screen screen) {}
}
