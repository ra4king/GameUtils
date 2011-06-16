package gameutils;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public abstract class AbstractInputListener implements InputListener {
	public void keyPressed(KeyEvent key) {}
	public void keyReleased(KeyEvent key) {}
	public void keyTyped(KeyEvent key) {}
	
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mousePressed(MouseEvent me) {}
	public void mouseReleased(MouseEvent me) {}
	public void mouseClicked(MouseEvent me) {}
	public void mouseDragged(MouseEvent me) {}
	
	public void mouseMoved(MouseEvent me) {}
	
	public void mouseWheelMoved(MouseWheelEvent mwe) {}
}