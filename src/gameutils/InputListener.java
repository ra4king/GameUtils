package gameutils;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * An all-in-one class that has all the basic input methods.
 * @author Roi Atalla
 */
public interface InputListener {
	public void keyPressed(KeyEvent key, Screen screen);
	public void keyReleased(KeyEvent key, Screen screen);
	public void keyTyped(KeyEvent key, Screen screen);
	
	public void mouseEntered(MouseEvent me, Screen screen);
	public void mouseExited(MouseEvent me, Screen screen);
	public void mousePressed(MouseEvent me, Screen screen);
	public void mouseReleased(MouseEvent me, Screen screen);
	public void mouseClicked(MouseEvent me, Screen screen);
	
	public void mouseDragged(MouseEvent me, Screen screen);
	public void mouseMoved(MouseEvent me, Screen screen);
	
	public void mouseWheelMoved(MouseWheelEvent mwe, Screen screen);
}