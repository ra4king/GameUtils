package gameutils;

import java.applet.Applet;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;


/**
 * The main class that should be extended by the user.
 * It handles the game loop and certain other functions.
 * @author Roi Atalla
 */
public class Game extends Applet implements Runnable {
	private static final long serialVersionUID = -1870725768768871166L;
	
	public static void main(String args[]) {
		Game game = new Game();
		game.setupFrame("Game",500,500,false);
		game.start();
	}
	
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
				stop();
			}
		});
		
		isApplet = false;
		
		setSize(width,height);
		
		init();
		
		((JPanel)frame.getContentPane()).revalidate();
		
		return frame;
	}
	
	/**
	 * Used to notify the game loop thread to update and render as fast as possible.
	 */
	public static final int MAX_FPS = 0;
	
	private final Art art;
	private final Sound sound;
	private final Map<String,ScreenInfo> screens;
	private ScreenInfo screenInfo;
	private final Canvas canvas;
	private final Input input;
	private Object quality;
	private int FPS;
	private double version;
	private boolean showFPS;
	private boolean standardKeysEnabled = true;
	private boolean isApplet = true;
	private volatile boolean isActive;
	private volatile boolean isPaused;
	
	/**
	 * Default constructor, sets the FPS to 60, version to 1.0, anti-aliasing is turned on, and the showFPS and gameOver properties are set to true.
	 */
	public Game() {
		this(60,1.0);
	}
	
	/**
	 * Sets the FPS and version.
	 * @param FPS The FPS to achieve.
	 * @param version The version of this game.
	 */
	public Game(int FPS, double version) {
		art = new Art();
		sound = new Sound();
		
		screens = new Hashtable<String,ScreenInfo>();
		
		screenInfo = new ScreenInfo(new Screen() {
			public void init(Game game) {}
			public void show() {}
			public void hide() {}
			public void update(long deltaTime) {}
			public void draw(Graphics2D g) {
				g.setColor(Color.lightGray);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		});
		
		canvas = new Canvas();
		input = new Input(canvas);
		
		this.FPS = FPS;
		this.version = version;
		showFPS = true;
		
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
		return isActive;
	}
	
	/**
	 * Manually pauses the game loop. paused() will be called
	 */
	public void pause() {
		isPaused = true;
		paused();
	}
	
	/**
	 * Returns true if the game loop is paused.
	 * @return True if the game loop is paused, false otherwise.
	 */
	public boolean isPaused() {
		return isPaused;
	}
	
	/**
	 * Manually resumes the game loop. resumed() will be called right before the game loop resumes.
	 */
	public void resume() {
		if(isActive && isPaused) {
			isPaused = false;
			resumed();
		}
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
		
		canvas.setSize(width,height);
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
		
		canvas.setSize(width,height);
	}
	
	private int currentFPS;
	
	/**
	 * Game loop.
	 */
	public final void run() {
		if(isActive())
			return;
		
		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch(Exception exc) {}
		
		if(isApplet())
			setSize(500,500);
		
		initGame();
		
		Listener listener = new Listener();
		canvas.addKeyListener(listener);
		canvas.addMouseListener(listener);
		canvas.addMouseMotionListener(listener);
		canvas.addMouseWheelListener(listener);
		
		BufferStrategy strategy = canvas.getBufferStrategy();
		
		int frames = 0;
		long time = System.nanoTime();
		long lastTime = System.nanoTime();
		
		isActive = true;
		
		canvas.requestFocusInWindow();
		
		while(isActive()) {
			long now = System.nanoTime();
			
			long diffTime = now-lastTime;
			
			while(diffTime > 0 && !isPaused()) {
				long deltaTime;
				
				if(FPS > 0) {
					long rem = diffTime%(1000000000/FPS);
					
					deltaTime = rem == 0 ? 1000000000/FPS : rem;
				}
				else
					deltaTime = diffTime;
				
				try{
					update(deltaTime);
				}
				catch(Exception exc) {
					exc.printStackTrace();
				}
				
				diffTime -= deltaTime;
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
							paint(g);
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
					while(System.nanoTime()-prevTime <= sleepTime-sleepTime/25)
						Thread.sleep(1);
				}
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
		}
	}
	
	/**
	 * Called as soon as this game is loaded.
	 */
	public final void init() {
		setIgnoreRepaint(true);
		setLayout(null);
		
		add(canvas);
		canvas.setIgnoreRepaint(true);
		canvas.createBufferStrategy(2);
		
		canvas.addFocusListener(new FocusListener() {
			private boolean focusLost;
			
			public void focusGained(FocusEvent fe) {
				if(focusLost && isPaused() && isActive()) {
					focusLost = false;
					resume();
				}
			}
			
			public void focusLost(FocusEvent fe) {
				if(!isPaused() && isActive()) {
					focusLost = true;
					pause();
				}
			}
		});
	}
	
	/**
	 * Automatically called if this game is an applet, otherwise it has to be manually called.
	 */
	public final void start() {
		if(!isActive())
			new Thread(this).start();
	}
	
	/**
	 * Empty method to be overrided. This is called as soon as start() is called.
	 */
	protected synchronized void initGame() {}
	
	/**
	 * Called FPS times a second. This method calls updateMenus or updateGameWorld according to the gameOver property.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public synchronized void update(long deltaTime) {
		screenInfo.screen.update(deltaTime);
	}
	
	/**
	 * Called when this game loses focus.
	 */
	protected synchronized void paused() {}
	
	/**
	 * Called when this game regains focus.
	 */
	protected synchronized void resumed() {}
	
	/**
	 * Called when this game is stopped.
	 */
	protected synchronized void stopGame() {}
	
	/**
	 * Called when this game is stopped. Calling this method stops the game loop. This method then calls stopGame().
	 */
	public final synchronized void stop() {
		sound.setOn(false);
		isActive = false;
		stopGame();
	}
	
	/**
	 * Called FPS times a second. Draws the Menus, the GameWorld, paused Menus, and/or the FPS according to the appropriate gameOver, isPaused, and showFPS properties.
	 * @param g The Graphics context to be used to draw to the canvas.
	 */
	public synchronized void paint(Graphics2D g) {
		Graphics2D g2 = (Graphics2D)g.create();
		
		if(screenInfo != null)
			screenInfo.screen.draw(g2);
		else
			g2.clearRect(0,0,getWidth(),getHeight());
		
		if(showFPS) {
			g2.setColor(java.awt.Color.black);
			g2.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.TRUETYPE_FONT,10));
			g2.drawString("Version " + version + "    " + currentFPS + " FPS",2,canvas.getHeight()-2);
		}
	}
	
	/**
	 * Adds a screen to this game.
	 * @param screen The Screen to add.
	 * @param name The name of the screen.
	 */
	public void addScreen(Screen screen, String name) {
		if(screen == null)
			throw new IllegalArgumentException("Screen cannot be null.");
		if(name == null)
			throw new IllegalArgumentException("Name cannot be null.");
		
		screens.put(name,new ScreenInfo(screen));
		screen.init(this);
	}
	
	/**
	 * Returns the current screen being shown.
	 * @return The current screen being shown.
	 */
	public Screen getScreen() {
		return screenInfo.screen;
	}
	
	/**
	 * Returns the screen specified
	 * @param name The name of the screen.
	 * @return The screen associated with the specified name.
	 */
	public Screen getScreen(String name) {
		return screens.get(name).screen;
	}
	
	/**
	 * Returns the name of the screen.
	 * @param screen The Screen who's name is returned.
	 * @return The name of the specified screen.
	 */
	public String getName(Screen screen) {
		for(String s : screens.keySet())
			if(screens.get(s).screen == screen)
				return s;
		return null;
	}
	
	/**
	 * Sets the current screen to the specified one.
	 * @param screen The Screen reference to switch to.
	 * @throws IllegalArgumentException If the specified screen has not already been added.
	 */
	public void setScreen(Screen screen) {
		setScreen(getScreenInfo(screen));
	}
	
	/**
	 * Sets the current screen to the specified one.
	 * @param name The name of the screen to switch to.
	 * @throws IllegalArgumentException If the specified name does not exist.
	 */
	public void setScreen(String name) {
		ScreenInfo info = screens.get(name);
		if(info == null)
			throw new IllegalArgumentException(name + " does not exist.");
		
		setScreen(info);
	}
	
	private void setScreen(ScreenInfo screenInfo) {
		if(screenInfo == null || !screens.containsValue(screenInfo))
			throw new IllegalArgumentException("Screen has not been added.");
		
		this.screenInfo.screen.hide();
		this.screenInfo = screenInfo;
		this.screenInfo.screen.show();
	}
	
	private ScreenInfo getScreenInfo(Screen screen) {
		if(screen == null)
			return null;
		
		for(ScreenInfo s : screens.values())
			if(s.screen == screen)
				return s;
		
		return null;
	}
	
	/**
	 * Adds an input listener on the specified screen.
	 * @param screen The Screen to add the listener to.
	 * @param listener The InputListener to be notified of input events on this screen.
	 */
	public void addInputListener(Screen screen, InputListener listener) {
		if(screen == null)
			throw new IllegalArgumentException("Screen cannot be null.");
		
		addInputListener(getScreenInfo(screen),listener);
	}
	
	/**
	 * Adds an input listener on the specified screen.
	 * @param name The name of the screen to add the listener to.
	 * @param listener The InputListener to be notified of input events on this screen.
	 */
	public void addInputListener(String name, InputListener listener) {
		ScreenInfo info = screens.get(name);
		if(info == null)
			throw new IllegalArgumentException(name + " does not exist.");
		
		addInputListener(screens.get(name),listener);
	}
	
	private synchronized void addInputListener(ScreenInfo screenInfo, InputListener listener) {
		if(screenInfo == null || !screens.containsValue(screenInfo))
			throw new IllegalArgumentException("Screen has not been added.");
		
		if(listener == null)
			throw new IllegalArgumentException("InputListener cannot be null.");
		
		screenInfo.listeners.add(listener);
	}
	
	/**
	 * Removes the input listener from the specified screen.
	 * @param screen The Screen to remove the listener from.
	 * @param listener The InputListener reference to remove.
	 */
	public void removeInputListener(Screen screen, InputListener listener) {
		if(screen == null)
			throw new IllegalArgumentException("Screen cannot be null.");
		
		removeInputListener(getScreenInfo(screen),listener);
	}
	
	/**
	 * Removes the input listener from the specified screen.
	 * @param name The name of the screen to remove the listener from.
	 * @param listener The InputListneer reference to remove.
	 */
	public void removeInputListener(String name, InputListener listener) {
		ScreenInfo info = screens.get(name);
		if(info == null)
			throw new IllegalArgumentException(name + " does not exist.");
		
		removeInputListener(screens.get(name),listener);
	}
	
	private synchronized void removeInputListener(ScreenInfo screenInfo, InputListener listener) {
		if(screenInfo == null || !screens.containsValue(screenInfo))
			throw new IllegalArgumentException("Screen has not been added.");
		
		if(listener == null)
			throw new IllegalArgumentException("InputListener cannot be null.");
		
		screenInfo.listeners.remove(listener);
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
	 * Returns a reference to the Art object.
	 * @return A reference to the Art object.
	 */
	public Art getArt() {
		return art;
	}
	
	/**
	 * Returns a reference to the Sound object.
	 * @return A reference to the Sound object.
	 */
	public Sound getSound() {
		return sound;
	}
	
	/**
	 * Returns a reference to the Input object.
	 * @return A reference to the Input object.
	 */
	public Input getInput() {
		return input;
	}
	
	private static class ScreenInfo {
		private Screen screen;
		private ArrayList<InputListener> listeners = new ArrayList<InputListener>();
		
		public ScreenInfo(Screen screen) {
			this.screen = screen;
		}
	}
	
	private class Listener implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
		public void keyTyped(KeyEvent key) {
			for(InputListener l : screenInfo.listeners)
				l.keyTyped(key);
		}
		
		public void keyPressed(KeyEvent key) {
			for(InputListener l : screenInfo.listeners)
				l.keyPressed(key);
			
			if(isStandardKeysEnabled()) {
				switch(key.getKeyCode()) {
					case KeyEvent.VK_M: sound.setOn(!sound.isOn()); break;
					case KeyEvent.VK_Q: setHighQuality(!isHighQuality()); break;
				}
			}
		}
		
		public void keyReleased(KeyEvent key) {
			for(InputListener l : screenInfo.listeners)
				l.keyReleased(key);
		}
		
		public void mouseClicked(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mouseClicked(me);
		}
		
		public void mouseEntered(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mouseEntered(me);
		}
		
		public void mouseExited(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mouseExited(me);
		}
		
		public void mousePressed(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mousePressed(me);
		}
		
		public void mouseReleased(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mouseReleased(me);
		}
		
		public void mouseDragged(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mouseDragged(me);
		}
		
		public void mouseMoved(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mouseMoved(me);
		}
		
		public void mouseWheelMoved(MouseWheelEvent mwe) {
			for(InputListener l : screenInfo.listeners)
				l.mouseWheelMoved(mwe);
		}
	}
}