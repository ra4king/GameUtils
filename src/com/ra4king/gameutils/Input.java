package com.ra4king.gameutils;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashSet;

/**
 * Handles key, mouse, and mouse motion input.
 * @author Roi Atalla
 */
public class Input {
	private HashSet<Integer> keys;
	private Point currentMouseLocation;
	private MouseEvent lastMousePressed, currentMousePressed;
	
	Input() {
		keys = new HashSet<Integer>();
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
		return currentMouseLocation;
	}
	
	/**
	 * Returns the last mouse pressed event since the last call to this method.
	 * @return The latest mouse click.
	 */
	public MouseEvent getLastMousePressed() {
		MouseEvent m = lastMousePressed;
		lastMousePressed = null;
		return m;
	}
	
	/**
	 * Returns the current position of the mouse if it is down. If the mouse has been released, returns null.
	 * @return The current position of the mouse if it is down, else null.
	 */
	public MouseEvent isMouseDown() {
		return currentMousePressed;
	}
	
	/**
	 * Clears the Set of keys and the mouse location and last mouse click are set to null.
	 */
	public void reset() {
		keys.clear();
		currentMouseLocation = null;
		lastMousePressed = currentMousePressed = null;
	}
	
	void keyPressed(KeyEvent key) {
		if(isKeyUp(key.getKeyCode()))
			keys.add(key.getKeyCode());
	}
	
	void keyReleased(KeyEvent key) {
		keys.remove(key.getKeyCode());
	}
	
	void mousePressed(MouseEvent me) {
		lastMousePressed = currentMousePressed = me;
	}
	
	void mouseReleased(MouseEvent me) {
		currentMousePressed = null;
	}
	
	void mouseMoved(MouseEvent me) {
		currentMouseLocation = me.getPoint();
	}
	
	void mouseDragged(MouseEvent me) {
		currentMouseLocation = me.getPoint();
	}
}