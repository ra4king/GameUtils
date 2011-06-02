package gameutils;

import java.awt.Canvas;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * The main class that should be extended by the user. It handles the game loop and certain other functions.
 * @author Roi Atalla
 */
public abstract class Game extends JApplet implements Runnable {
	private static final long serialVersionUID = -1870725768768871166L;
	
	/**
	 * Initializes and displays the window, then calls init().
	 * @param title The title of the window
	 * @param width The width of the window
	 * @param height The height of the window
	 * @param resizable If true, the window will be resizable, else it will not be resizable.
	 * @return The JFrame that was initialized by this method.
	 */
	protected final JFrame setupFrame(String title, int width, int height, boolean resizable) {
		final JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIgnoreRepaint(true);
		frame.add(this);
		frame.setResizable(resizable);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				gameOver = true;
				setSoundOn(false);
				stop();
			}
		});
		
		isApplet = false;
		
		setSize(width,height);
		
		init();
		
		return frame;
	}
	
	/**
	 * To be used with the <code>addInputListener</code> method.
	 */
	public static final int MENUS = 0;
	
	/**
	 * To be used with the <code>addInputListener</code> method.
	 */
	public static final int GAMEWORLD = 1;
	
	/**
	 * To be used with the <code>addInputListener</code> method.
	 */
	public static final int PAUSEMENUS = 2;
	
	/**
	 * To be used with the <code>addInputListener</code> method.
	 */
	public static final int GLOBAL = 3;
	
	private ArrayList<InputListener> menusListeners;
	private ArrayList<InputListener> gwListeners;
	private ArrayList<InputListener> pauseMenusListeners;
	private GameWorld gameWorld;
	private Menus menus;
	private Menus pauseMenus;
	private Canvas canvas;
	private Input input;
	private Object quality;
	private int FPS;
	private double version;
	private boolean isSoundOn;
	private boolean isPaused;
	private boolean gameOver;
	private boolean showFPS;
	private boolean standardKeysEnabled = true;
	private boolean isApplet = true;
	
	/**
	 * Default constructor, sets the FPS to 60, version to 1.0, anti-aliasing is turned on, and the showFPS and gameOver properties are set to true.
	 */
	public Game() {
		menusListeners = new ArrayList<InputListener>();
		gwListeners = new ArrayList<InputListener>();
		pauseMenusListeners = new ArrayList<InputListener>();
		
		this.FPS = 60;
		this.version = 1.0;
		showFPS = true;
		gameOver = true;
		
		quality = RenderingHints.VALUE_ANTIALIAS_ON;
	}
	
	/**
	 * If this game is an applet, it calls the super class's getCodeBase(),
	 * else the current directory is returned.
	 */
	public URL getCodeBase() {
		if(isApplet())
			return super.getCodeBase();
		
		try{
			return new URL("file:///"+System.getProperty("user.dir"));
		}
		catch(Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns the canvas's Graphics object.
	 */
	public Graphics getGraphics() {
		if(canvas == null)
			return super.getGraphics();
		return canvas.getGraphics();
	}
	
	/**
	 * Returns the root component.
	 * @return If this game is an applet, it returns this, else it returns the JFrame used to display this game.
	 */
	public Container getHighestParent() {
		if(isApplet())
			return this;
		return getParent().getParent().getParent().getParent();
	}
	
	/**
	 * @return Returns true if this game is an applet, false otherwise.
	 */
	public boolean isApplet() {
		return isApplet;
	}
	
	/**
	 * @return Returns true if this game is currently active.
	 */
	public boolean isActive() {
		if(isApplet())
			return super.isActive();
		return true;
	}
	
	/**
	 * If this game is an applet, it calls the superclass's resize method, else it calls setSize(int,int).
	 * @param width The new width of this game's canvas.
	 * @param height The new height of this game's canvas;
	 */
	public void resize(int width, int height) {
		if(isApplet())
			super.resize(width,height);
		else
			setSize(width,height);
	}
	
	/**
	 * If this game is an applet, it calls the superclass's resize method, else it adjusts the JFrame according to the platform specific inset values.
	 * @param width The new width of this game's canvas
	 * @param height The new height of this game's canvas
	 */
	public void setSize(int width, int height) {
		if(isApplet())
			super.resize(width,height);
		else {
			Insets i = getHighestParent().getInsets();
			getHighestParent().setSize(width+i.right+i.left,height+i.bottom+i.top);
			((JFrame)getHighestParent()).setLocationRelativeTo(null);
		}
	}
	
	/**
	 * Called as soon as this game is loaded.
	 */
	public final void init() {
		setIgnoreRepaint(true);
		
		canvas = (Canvas)add(new Canvas());
		canvas.createBufferStrategy(2);
		
		gameWorld = new GameWorld(this);
		menus = new Menus(this);
		pauseMenus = new Menus(this);
	}
	
	/**
	 * Empty method to be overrided. This is called as soon as start() is called.
	 */
	protected void initGame() {}
	
	/**
	 * Automatically called if this game is an applet, else it has to be manually called.
	 */
	public void start() {
		new Thread(this).start();
	}
	
	/**
	 * Called when this game is stopped. Also called when the JFrame is closed.
	 */
	public void stop() {
	}
	
	private int currentFPS;
	
	/**
	 * Game loop.
	 */
	public final void run() {
		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch(Exception exc) {}
		
		if(isApplet())
			setSize(500,500);
		
		initGame();
		
		input = new Input(canvas);
		
		Listener listener = new Listener();
		canvas.addKeyListener(listener);
		canvas.addMouseListener(listener);
		canvas.addMouseMotionListener(listener);
		canvas.addMouseWheelListener(listener);
		
		BufferStrategy strategy = canvas.getBufferStrategy();
		
		int frames = 0;
		long time = System.nanoTime();
		long lastTime = System.nanoTime();
		
		while(isActive()) {
			long now = System.nanoTime();
			
			if(!isPaused) {
				long diffTime = now-lastTime;
				
				while(diffTime > 0) {
					long rem = diffTime%(1000000000/FPS);
					
					long deltaTime = rem == 0 ? 1000000000/FPS : rem;
					
					synchronized(this) {
						update(deltaTime);
					}
					
					diffTime -= deltaTime;
				}
			}
			
			lastTime = now;
			
			if(System.nanoTime()-time >= 1000000000) {
				time = System.nanoTime();
				currentFPS = frames;
				frames = 0;
			}
			
			frames++;
			
			try{
				do{
					do{
						Graphics2D g = (Graphics2D)strategy.getDrawGraphics();
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,quality);
						
						try{
							synchronized(this) {
								paint(g);
							}
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
						
						g.dispose();
					}while(strategy.contentsRestored());
					
					strategy.show();
				}while(strategy.contentsLost());
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
			
			try{
				if(FPS > 0) {
					long sleepTime = Math.round((1000000000.0/FPS)-(System.nanoTime()-lastTime));
					if(sleepTime <= 0)
						continue;
					
					long prevTime = System.nanoTime();
					while(System.nanoTime()-prevTime <= sleepTime) {
						Thread.yield();
						Thread.sleep(1);
					}
				}
				else
					Thread.yield();
			}
			catch(Exception exc) {}
		}
	}
	
	/**
	 * Called FPS times a second. This method calls updateMenus or updateGameWorld according to the gameOver property.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public void update(long deltaTime) {
		if(gameOver)
			updateMenus(deltaTime);
		else
			updateGameWorld(deltaTime);
	}
	
	/**
	 * Called by update() when the current gameOver property is true.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public void updateMenus(long deltaTime) {}
	
	/**
	 * called by update() when the current gameOver property is false. The default implementation just calls <code>gameWorld.updateComponents(deltaTime)</code>.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public void updateGameWorld(long deltaTime) {
		gameWorld.updateComponents(deltaTime);
	}
	
	/**
	 * Called FPS times a second. Draws the Menus, the GameWorld, paused Menus, and/or the FPS according to the appropriate gameOver, isPaused, and showFPS properties.
	 * @param g The Graphics context to be used to draw to the canvas.
	 */
	public void paint(Graphics2D g) {
		Graphics2D g2 = (Graphics2D)g.create();
		if(gameOver)
			menus.draw(g2);
		else
			gameWorld.draw(g2);
		
		if(isPaused)
			pauseMenus.draw(g2);
		
		if(showFPS) {
			g2.setColor(java.awt.Color.black);
			g2.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.TRUETYPE_FONT,10));
			g2.drawString("Version " + version + "    " + currentFPS + " FPS",2,canvas.getHeight()-2);
		}
	}
	
	/**
	 * Adds an input listener on the specified screen.
	 * @param option The screen to add the InputListener. The current options are GAMEWORLD, MENUS, PAUSEMENUS, and GLOBAL.
	 * @param listener The InputListener to be notified of input events.
	 */
	public void addInputListener(int option, InputListener listener) {
		switch(option) {
			case GLOBAL: gwListeners.add(listener); pauseMenusListeners.add(listener);
			case MENUS: menusListeners.add(listener); break;
			case GAMEWORLD: gwListeners.add(listener); break;
			case PAUSEMENUS: pauseMenusListeners.add(listener); break;
			default: throw new IllegalArgumentException("Invalid option: " + option);
		}
	}
	
	/**
	 * Removes the input listener from the specified screen.
	 * @param option The screen to remove the InputListener from. The current options are GAMEWORLD, MENUS, PAUSEMENUS, and GLOBAL.
	 * @param listener The InputListener reference to remove.
	 */
	public void removeInputListener(int option, InputListener listener) {
		switch(option) {
			case GLOBAL: gwListeners.remove(listener); pauseMenusListeners.remove(listener);
			case MENUS: menusListeners.remove(listener); break;
			case GAMEWORLD: gwListeners.remove(listener); break;
			case PAUSEMENUS: pauseMenusListeners.remove(listener); break;
			default: throw new IllegalArgumentException("Invalid option: " + option);
		}
	}
	
	/**
	 * Turns audio on/off.
	 * @param isSoundOn If true, audio is on, else audio is off.
	 */
	public void setSoundOn(boolean isSoundOn) {
		this.isSoundOn = isSoundOn;
		
		Sound.getSound().setOn(isSoundOn);
	}
	
	/**
	 * Returns the current state of the isSoundOn property.
	 * @return If true, audio is on, else audio is off.
	 */
	public boolean isSoundOn() {
		return isSoundOn;
	}
	
	/**
	 * Pauses or resumes the game. When the game is paused, the pause() method is called on the Sound object.
	 * @param isPaused If true, the game is paused by stopping the calls to update and drawing the pauseMenus, else the game is resumed. 
	 */
	public synchronized void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
		
		if(isPaused)
			Sound.getSound().pause();
		else
			Sound.getSound().resume();
	}
	
	/**
	 * Returns the current state of the isPaused property.
	 * @return If true, the game is paused, else the game is running.
	 */
	public boolean isPaused() {
		return isPaused;
	}
	
	/**
	 * Sets the gameOver property.
	 * @param gameOver If true, the Menus screen is displayed, else the GameWorld screen is displayed.
	 */
	public synchronized void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
		
		menus.setActive(gameOver);
	}
	
	/**
	 * Returns the current state of the gameOver property.
	 * @return If true, the Menus screen is displayed, else the GameWorld screen is displayed.
	 */
	public boolean isGameOver() {
		return gameOver;
	}
	
	/**
	 * Sets the version of the game.
	 * @param version The current version of the game.
	 */
	public void setVersion(double version) {
		this.version = version;
	}
	
	/**
	 * Returns the version of the game.
	 * @return The current version of the game.
	 */
	public double getVersion() {
		return version;
	}
	
	/**
	 * Sets the showFPS property.
	 * @param showFPS If true, the FPS is updated and displayed every second, else the FPS is not displayed.
	 */
	public void showFPS(boolean showFPS) {
		this.showFPS = showFPS;
	}
	
	/**
	 * Returns the current state of the showFPS property.
	 * @return If true, the FPS is updated and displayed every second, else the FPS is not displayed.
	 */
	public boolean isShowingFPS() {
		return showFPS;
	}
	
	/**
	 * Sets the optimal FPS of this game.
	 * @param FPS Specifies the number of updates and frames shown per second.
	 */
	public void setFPS(int FPS) {
		this.FPS = FPS;
	}
	
	/**
	 * Returns the optimal FPS of this game.
	 * @return The number of udpates and frames shown per second.
	 */
	public int getFPS() {
		return FPS;
	}
	
	/**
	 * Sets the quality of this game's graphics.
	 * @param highQuality If true, the graphics are of high quality, else the graphics are of low quality.
	 */
	public void setHighQuality(boolean highQuality) {
		if(highQuality)
			quality = RenderingHints.VALUE_ANTIALIAS_ON;
		else
			quality = RenderingHints.VALUE_ANTIALIAS_OFF;
	}
	
	/**
	 * Returns the current state of the quality property.
	 * @return If true, the graphics are of high quality, else the graphics are of low quality.
	 */
	public boolean isHighQuality() {
		return quality == RenderingHints.VALUE_ANTIALIAS_ON;
	}
	
	/**
	 * The standard keys are M for audio on/off, P for pause/resume, and Q for high/low quality.
	 * @param standardKeysEnabled If true, a key press of the standard keys automatically call the appropriate methods, else this function is disabled.
	 */
	public void setStandardKeysEnabled(boolean standardKeysEnabled) {
		this.standardKeysEnabled = standardKeysEnabled;
	}
	
	/**
	 * Returns the current state of the standardKeysEnabled property.
	 * @return If true, a key press of the standard keys automatically call the appropriate methods, else this function is disabled.
	 */
	public boolean isStandardKeysEnabled() {
		return standardKeysEnabled;
	}
	
	/**
	 * Returns a reference to the Input object.
	 * @return A reference to the Input object.
	 */
	public Input getInput() {
		return input;
	}
	
	/**
	 * Returns a reference to the GameWorld object.
	 * @return A reference to the GameWorld object.
	 */
	public GameWorld getGameWorld() {
		return gameWorld;
	}
	
	/**
	 * Returns a reference to the Menus object.
	 * @return A reference to the Menus object.
	 */
	public Menus getMenus() {
		return menus;
	}
	
	/**
	 * Returns a reference to the Menus object used as the pauseMenu.
	 * @return A reference to the Menus object used as the pauseMenu.
	 */
	public Menus getPauseMenus() {
		return pauseMenus;
	}
	
	private class Listener implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
		public void keyTyped(KeyEvent key) {}
		
		public void keyPressed(KeyEvent key) {
			ArrayList<InputListener> listeners;
			if(gameOver)
				listeners = menusListeners;
			else if(!isPaused)
				listeners = gwListeners;
			else
				listeners = pauseMenusListeners;
			
			for(InputListener l : listeners)
				l.keyPressed(key);
			
			if(!gameOver && isStandardKeysEnabled()) {
				switch(key.getKeyCode()) {
					case KeyEvent.VK_M: setSoundOn(!isSoundOn); break;
					case KeyEvent.VK_P: setPaused(!isPaused()); break;
					case KeyEvent.VK_Q:
						setHighQuality(!isHighQuality());
						if(isSoundOn())
							Sound.getSound().resume();
						else
							Sound.getSound().pause();
				}
			}
		}
		
		public void keyReleased(KeyEvent key) {
			ArrayList<InputListener> listeners;
			if(gameOver)
				listeners = menusListeners;
			else if(!isPaused)
				listeners = gwListeners;
			else
				listeners = pauseMenusListeners;
			
			for(InputListener l : listeners)
				l.keyReleased(key);
		}
		
		public void mouseClicked(MouseEvent me) {}
		
		public void mouseEntered(MouseEvent me) {
			ArrayList<InputListener> listeners;
			if(gameOver)
				listeners = menusListeners;
			else if(!isPaused)
				listeners = gwListeners;
			else
				listeners = pauseMenusListeners;
			
			for(InputListener l : listeners)
				l.mouseEntered(me);
		}
		
		public void mouseExited(MouseEvent me) {
			ArrayList<InputListener> listeners;
			if(gameOver)
				listeners = menusListeners;
			else if(!isPaused)
				listeners = gwListeners;
			else
				listeners = pauseMenusListeners;
			
			for(InputListener l : listeners)
				l.mouseExited(me);
		}
		
		public void mousePressed(MouseEvent me) {
			ArrayList<InputListener> listeners;
			if(gameOver)
				listeners = menusListeners;
			else if(!isPaused)
				listeners = gwListeners;
			else
				listeners = pauseMenusListeners;
			
			for(InputListener l : listeners)
				l.mousePressed(me);
		}
		
		public void mouseReleased(MouseEvent me) {
			ArrayList<InputListener> listeners;
			if(gameOver)
				listeners = menusListeners;
			else if(!isPaused)
				listeners = gwListeners;
			else
				listeners = pauseMenusListeners;
			
			for(InputListener l : listeners)
				l.mouseReleased(me);
		}
		
		public void mouseDragged(MouseEvent me) {
			ArrayList<InputListener> listeners;
			if(gameOver)
				listeners = menusListeners;
			else if(!isPaused)
				listeners = gwListeners;
			else
				listeners = pauseMenusListeners;
			
			for(InputListener l : listeners)
				l.mouseDragged(me);
		}
		
		public void mouseMoved(MouseEvent me) {
			ArrayList<InputListener> listeners;
			if(gameOver)
				listeners = menusListeners;
			else if(!isPaused)
				listeners = gwListeners;
			else
				listeners = pauseMenusListeners;
			
			for(InputListener l : listeners)
				l.mouseMoved(me);
		}
		
		public void mouseWheelMoved(MouseWheelEvent me) {
			ArrayList<InputListener> listeners;
			if(gameOver)
				listeners = menusListeners;
			else if(!isPaused)
				listeners = gwListeners;
			else
				listeners = pauseMenusListeners;
			
			for(InputListener l : listeners)
				l.mouseWheelMoved(me);
		}
	}
}