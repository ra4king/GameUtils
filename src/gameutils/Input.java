package gameutils;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles key, mouse, and mouse motion input.
 * @author Roi Atalla
 */
public class Input {
	private Set<Integer> keys = Collections.synchronizedSet(new HashSet<Integer>());
	private Point currentMouseLoc, lastMouseClick, mouseDown;
	
	/**
	 * Initializes the listeners on the specified component.
	 * @param comp
	 */
	public Input(Component comp) {
		Listener l = new Listener();
		comp.addKeyListener(l);
		comp.addMouseListener(l);
		comp.addMouseMotionListener(l);
	}
	
	/**
	 * Checks if the specified key is currently pressed.
	 * @param key The key to check if it is currently pressed. The keys used are in the KeyEvent class.
	 * @return True if the key is pressed, false otherwise.
	 */
	public boolean isKeyDown(int key) {
		return keys.contains(key);
	}
	
	/**
	 * Checks if the specified key is not currently pressed.
	 * @param key The key to check if it is not currently pressed. The keys used are in the KeyEvent class.
	 * @return True if the key is not pressed, false otherwise.
	 */
	public boolean isKeyUp(int key) {
		return !isKeyDown(key);
	}
	
	/**
	 * Returns the current mouse location.
	 * @return The current mouse location.
	 */
	public Point getCurrentMouseLocation() {
		return currentMouseLoc;
	}
	
	/**
	 * Returns the last mouse click. Then it is set to null;
	 * @return The latest mouse click.
	 */
	public Point getLastMouseClick() {
		Point p = lastMouseClick;
		lastMouseClick = null;
		return p;
	}
	
	/**
	 * Returns the current position of the mouse if it is down. If the mouse has been released, returns null;
	 * @return The current position of the mouse if it is down, else null.
	 */
	public Point isMouseDown() {
		return mouseDown;
	}
	
	/**
	 * Clears the Set of keys and the mouse location and last mouse click are set to null.
	 */
	public void reset() {
		keys.clear();
		currentMouseLoc = lastMouseClick = mouseDown = null;
	}
	
	private class Listener implements KeyListener, MouseListener, MouseMotionListener {
		public void keyPressed(KeyEvent key) {
			if(isKeyUp(key.getKeyCode()))
				keys.add(key.getKeyCode());
		}
		
		public void keyReleased(KeyEvent key) {
			keys.remove(key.getKeyCode());
		}
		
		public void keyTyped(KeyEvent key) {}
		
		public void mouseClicked(MouseEvent me) {}
		
		public void mouseEntered(MouseEvent me) {}
		
		public void mouseExited(MouseEvent me) {}
		
		public void mousePressed(MouseEvent me) {
			lastMouseClick = mouseDown = me.getPoint();
		}
		
		public void mouseReleased(MouseEvent me) {
			mouseDown = null;
		}
		
		public void mouseMoved(MouseEvent me) {
			currentMouseLoc = me.getPoint();
		}
		
		public void mouseDragged(MouseEvent me) {
			currentMouseLoc = me.getPoint();
		}
	}
}