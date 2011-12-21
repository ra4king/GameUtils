package com.ra4king.gameutils;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Implements the methods in InputListener so you wouldn't have to implement the ones you don't need.
 * @author Roi Atalla
 */
public abstract class InputAdapter implements InputListener {
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