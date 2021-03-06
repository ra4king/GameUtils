package com.ra4king.gameutils;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Takes care of most abstract methods.
 * @author Roi Atalla
 */
public abstract class BasicScreen implements Screen {
	private Game game;
	
	/**
	 * Stores the reference to [code]game[/code] and adds itself as an InputListener.
	 */
	@Override
	public void init(Game game) {
		this.game = game;
		
		if(game.isScreen(this))
			game.addInputListener(this, new InputListener() {
				@Override
				public void keyPressed(KeyEvent key, Screen screen) {
					BasicScreen.this.keyPressed(key);
				}
				
				@Override
				public void keyReleased(KeyEvent key, Screen screen) {
					BasicScreen.this.keyReleased(key);
				}
				
				@Override
				public void keyTyped(KeyEvent key, Screen screen) {
					BasicScreen.this.keyTyped(key);
				}
				
				@Override
				public void mouseEntered(MouseEvent me, Screen screen) {
					BasicScreen.this.mouseEntered(me);
				}
				
				@Override
				public void mouseExited(MouseEvent me, Screen screen) {
					BasicScreen.this.mouseExited(me);
				}
				
				@Override
				public void mousePressed(MouseEvent me, Screen screen) {
					BasicScreen.this.mousePressed(me);
				}
				
				@Override
				public void mouseReleased(MouseEvent me, Screen screen) {
					BasicScreen.this.mouseReleased(me);
				}
				
				@Override
				public void mouseClicked(MouseEvent me, Screen screen) {
					BasicScreen.this.mouseClicked(me);
				}
				
				@Override
				public void mouseDragged(MouseEvent me, Screen screen) {
					BasicScreen.this.mouseDragged(me);
				}
				
				@Override
				public void mouseMoved(MouseEvent me, Screen screen) {
					BasicScreen.this.mouseMoved(me);
				}
				
				@Override
				public void mouseWheelMoved(MouseWheelEvent mwe, Screen screen) {
					BasicScreen.this.mouseWheelMoved(mwe);
				}
			});
	}
	
	@Override
	public Game getGame() {
		return game;
	}
	
	public int getWidth() {
		return game.getWidth();
	}
	
	public int getHeight() {
		return game.getHeight();
	}
	
	@Override
	public void show() {}
	
	@Override
	public void hide() {}
	
	@Override
	public void paused() {}
	
	@Override
	public void resumed() {}
	
	@Override
	public void resized(int width, int height) {}
	
	@Override
	public abstract void update(long deltaTime);
	
	@Override
	public abstract void draw(Graphics2D g);
	
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
