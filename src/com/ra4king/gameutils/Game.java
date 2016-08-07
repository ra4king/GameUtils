package com.ra4king.gameutils;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Game is the main class that must be extended by the user. It handles the game loop and certain other functions.<br>
 * Game extends Applet but also supports being a desktop app.<br>
 * Remember: if this game will be used as an Applet, a default constructor <strong>MUST</strong> be present.<br>
 * <br>
 * It uses a Screen system where only 1 Screen is active at one time.<br>
 * <br>
 * A typical game looks like this: <br>
 * <br>
 * <code>
 * public class MyGame extends Game {<br>
 * &#09;public static void main(String args[]) {<br>
 * &#09;&#09;MyGame game = new MyGame();<br>
 * &#09;&#09;game.setupFrame("My Game",true);<br>
 * &#09;&#09;game.start();<br>
 * &#09;}<br>
 * <br>
 * &#09;public MyGame() {<br>
 * &#09;&#09;super(800,600);<br>
 * &#09;}<br>
 * <br>
 * &#09;public void initGame() {<br>
 * &#09;&#09;//Initialize the game<br>
 * &#09;}<br>
 * <br>
 * &#09;public void update(long deltaTime) {<br>
 * &#09;&#09;super.update(deltaTime);<br>
 * &#09;&#09;//Optional: update the game<br>
 * &#09;}<br>
 * <br>
 * &#09;public void paused() {<br>
 * &#09;&#09;//Pause the game<br>
 * &#09;}<br>
 * <br>
 * &#09;public void resumed() {<br>
 * &#09;&#09;//Resume the game<br>
 * &#09;}<br>
 * <br>     
 * &#09;public void resized(int width, int height) {<br>
 * &#09;&#09;//Resize the game<br>
 * &#09;}<br>
 * <br>     
 * &#09;public void stopGame() {<br>
 * &#09;&#09;//Stop the game<br>
 * &#09;}<br>
 * <br>     
 * &#09;public void paint(Graphics2D g) {<br>
 * &#09;&#09;super.paint(g);<br>
 * &#09;&#09;//Optional: draw the game<br>
 * &#09;}<br>
 * }
 * </code>
 * @author Roi Atalla
 */
public abstract class Game extends Applet {
	private static final long serialVersionUID = -1870725768768871166L;
	
	static {
		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch(Exception exc) {}
		
		if(System.getProperty("os.name").startsWith("Win")) {
			new Thread() {
				{
					setDaemon(true);
					start();
				}
				
				@Override
				public void run() {
					while(true) {
						try {
							Thread.sleep(Long.MAX_VALUE);
						}
						catch(Exception exc) {}
					}
				}
			};
		}
	}
	
	/**
	 * Initializes and displays the window.
	 * @param title The title of the window
	 * @param resizable If true, the window will be resizable, else it will not be resizable.
	 * @return The Frame that was initialized by this method.
	 */
	protected final Frame setupFrame(String title, boolean resizable) {
		Frame frame = new Frame(title);
		frame.setIgnoreRepaint(true);
		frame.setResizable(resizable);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				stop();
			}
		});
		
		frame.add(this);
		frame.setVisible(true);
		
		isApplet = false;
		
		setSize(width,height);
		
		init();
		
		return frame;
	}
	
	/**
	 * Used to notify the game loop thread to update and render as fast as possible.
	 */
	public static final int MAX_FPS = 0;
	
	/**
	 * Used to notify the game loop thread to update the maximum number of times before render.
	 */
	public static final int MAX_UPDATES = -1;
	
	/**
	 * 1 second in nanoseconds, AKA 1,000,000,000 nanoseconds.
	 */
	public static final long ONE_SECOND = (long)1e9;
	
	private final Art art;
	private final Sound sound;
	
	private final ArrayList<Callback> callbacks;
	
	private final HashMap<String,ScreenInfo> screens;
	private ScreenInfo currentScreen;
	
	private final Canvas canvas;
	private BufferStrategy strategy;
	
	private final Input input;
	private ConcurrentLinkedQueue<Event> events;
	
	private boolean isApplet = true;
	private int width, height;
	
	private int FPS, maxUpdates;
	private double version;
	private boolean showFPS;
	private volatile boolean isActive;
	private volatile boolean isPaused;
	
	private ArrayList<TempListener> tempListeners;
	private volatile boolean processingEvents;
	
	public Game(int width, int height) {
		this(width,height,60,1.0);
	}
	
	public Game(int width, int height, int FPS) {
		this(width,height,FPS,1.0);
	}
	
	/**
	 * Sets the width and height of the game.
	 * @param width
	 * @param height
	 */
	public Game(int width, int height, int FPS, double version) {
		setIgnoreRepaint(true);
		
		art = new Art();
		sound = new Sound();
		
		callbacks = new ArrayList<Callback>();
		
		screens = new HashMap<String,ScreenInfo>();
		
		currentScreen = new ScreenInfo(new Screen() {
			@Override
			public void init(Game game) {}
			@Override
			public Game getGame() { return Game.this; }
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
			public void update(long deltaTime) {}
			@Override
			public void draw(Graphics2D g) {}
		});
		
		screens.put("", currentScreen);
		
		canvas = new Canvas();
		input = new Input();
		
		events = new ConcurrentLinkedQueue<Event>();
		
		tempListeners = new ArrayList<TempListener>();
		
		this.width = width;
		this.height = height;
		setFPS(FPS);
		setVersion(version);
		
		showFPS = true;
	}
	
	/**
	 * Returns the current working directory of this game.
	 * @return The current working directory of this game.
	 */
	@Override
	public URL getCodeBase() {
		if(isApplet())
			return super.getCodeBase();
		
		try{
			return getClass().getResource("/");
		}
		catch(Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Graphics getGraphics() {
		return canvas.getGraphics();
	}
	
	/**
	 * Returns the appropriate container of this game: this instance if it is an Applet, the JFrame if it is a desktop application.
	 * @return If this game is an Applet, it returns this instance, else it returns the JFrame used to display this game.
	 */
	public Container getRootParent() {
		if(isApplet())
			return this;
		return getParent();
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	/**
	 * @return Returns true if this game is an Applet, false otherwise.
	 */
	public boolean isApplet() {
		return isApplet;
	}
	
	/**
	 * @return Returns true if this game is currently active.
	 */
	@Override
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * Manually pauses the game loop. paused() will be called
	 */
	public void pause() {
		if(isActive() && !isPaused()) {
			isPaused = true;
			paused();
		}
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
		if(isActive() && isPaused()) {
			isPaused = false;
			resumed();
		}
	}
	
	/**
	 * If this game is an applet, it calls the superclass's resize method, else it calls setSize(int,int).
	 * @param width The new width of this game's canvas.
	 * @param height The new height of this game's canvas;
	 */
	@Override
	public void resize(int width, int height) {
		setSize(width,height);
	}
	
	/**
	 * If this game is an Applet, it calls the superclass's resize method, else it adjusts the JFrame accordingly.
	 * @param width The new width of this game's canvas
	 * @param height The new height of this game's canvas
	 */
	@Override
	public void setSize(int width, int height) {
		if(isApplet())
			super.resize(width,height);
		else {
			setPreferredSize(new Dimension(width,height));
			((Frame)getRootParent()).pack();
			((Frame)getRootParent()).setLocationRelativeTo(null);
		}
		
		if(isActive) {
			this.width = width;
			this.height = height;
		}
		
		invalidate();
		validate();
	}
	
	/**
	 * Sets the game to full screen.
	 * @param setFullScreen If true, it is set to full screen, else it is returned to windowed mode.
	 * @throws IllegalStateException If setFullScreen is true and isFullScreen() returns true.
	 */
	public void setFullScreen(boolean setFullScreen) {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		
		if(setFullScreen) {
			if(isFullScreen())
				return;
			
			Frame frame = new Frame();
			frame.setResizable(false);
			frame.setUndecorated(true);
			frame.setIgnoreRepaint(true);
			
			remove(canvas);
			invalidate();
			validate();
			
			frame.add(canvas);
			
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent we) {
					events.add(new Event(14,we));
				}
			});
			
			gd.setFullScreenWindow(frame);
			
			canvas.createBufferStrategy(2);
			strategy = canvas.getBufferStrategy();
		}
		else {
			if(!isFullScreen())
				return;
			
			Frame f = (Frame)gd.getFullScreenWindow();
			f.remove(canvas);
			f.dispose();
			
			gd.setFullScreenWindow(null);
			
			add(canvas);
			invalidate();
			validate();
			
			canvas.createBufferStrategy(2);
			strategy = canvas.getBufferStrategy();
		}
		
		width = canvas.getWidth();
		height = canvas.getHeight();
		
		canvas.requestFocus();
		canvas.requestFocusInWindow();
	}
	
	/**
	 * Returns true if this game is running full screen.
	 * @return True if this game is running full screen, false otherwise.
	 */
	public boolean isFullScreen() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getFullScreenWindow() != null;
	}
	
	final void gameLoop() {
		if(isActive())
			return;
		
		Thread.currentThread().setName("Game Loop Thread");
		
		try {
			setSize(width,height);
			
			initGame();
			
			getScreen().show();
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		
		Listener listener = new Listener();
		canvas.addKeyListener(listener);
		canvas.addMouseListener(listener);
		canvas.addMouseMotionListener(listener);
		canvas.addMouseWheelListener(listener);
		
		if(strategy == null) {
			canvas.createBufferStrategy(2);
			strategy = canvas.getBufferStrategy();
		}
		
		Font fpsFont = new Font(Font.SANS_SERIF,Font.TRUETYPE_FONT,10);
		
		int frames = 0;
		int currentFPS = 0;
		long time = System.nanoTime();
		long lastTime = System.nanoTime();
		
		isActive = true;
		
		canvas.requestFocus();
		
		while(true) {
			try {
				processCallbacks();
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
			
			try{
				processEvents();
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
			
			long diffTime = System.nanoTime()-lastTime;
			lastTime += diffTime;
			
			if(!isPaused()) {
				while(diffTime > 0) {
					int fps = FPS > 0 ? FPS : 60;
					long deltaTime = Math.min(diffTime,Math.round(ONE_SECOND/(double)fps));
					
					try{
						update(deltaTime);
					}
					catch(Exception exc) {
						exc.printStackTrace();
					}
					
					diffTime -= deltaTime;
				}
			}
			
			try{
				do{
					do{
						Graphics2D g = (Graphics2D)strategy.getDrawGraphics();
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
						
						try{
							paint(g);
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
						
						if(showFPS) {
							g.setFont(fpsFont);
							g.drawString("Version " + version + "    " + currentFPS + " FPS",2,getHeight()-2);
						}
						
						g.dispose();
					}while(strategy.contentsRestored());
					
					strategy.show();
				}while(strategy.contentsLost());
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
			
			frames++;
			
			if(System.nanoTime()-time >= ONE_SECOND) {
				time += ONE_SECOND;
				currentFPS = frames;
				frames = 0;
			}
			
			try{
				if(FPS > 0) {
					long sleepTime = Math.round((ONE_SECOND/FPS)-(System.nanoTime()-lastTime));
					if(sleepTime <= 0)
						continue;
					
					long prevTime = System.nanoTime(), timePassed;
					while((timePassed = System.nanoTime()-prevTime) < sleepTime) {
						if(timePassed <= sleepTime * 0.8)
							Thread.sleep(1);
						else
							Thread.yield();
					}
				}
				else
					Thread.yield();
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
			
			if(!isActive()) {
				boolean stop = stopGame();
				if(stop) {
					if(isApplet())
						throw new RuntimeException("Cannot stop an applet.");
					else
						System.exit(0);
				}
				else
					isActive = true;
			}
		}
	}
	
	private void processCallbacks() {
		for(int a = 0; a < callbacks.size(); a++) {
			Callback c = callbacks.get(a);
			
			if(System.nanoTime()-c.lastTime >= c.delay) {
				try {
					c.r.run();
				}
				catch(Exception exc) {
					exc.printStackTrace();
				}
				
				c.lastTime += c.delay;
			}
		}
	}
	
	private void processEvents() {
		processingEvents = true;
		
		while(!events.isEmpty()) {
			Event e = events.poll();
			
			switch(e.id) {
				case 0:
					for(InputListener l : currentScreen.listeners) {
						try {
							l.keyTyped((KeyEvent)e.event,getScreen());
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
					}
					
					break;
				case 1:
					input.keyPressed((KeyEvent)e.event);
					
					for(InputListener l : currentScreen.listeners) {
						try {
							l.keyPressed((KeyEvent)e.event,getScreen());
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
					}
					
					break;
				case 2:
					input.keyReleased((KeyEvent)e.event);
					
					for(InputListener l : currentScreen.listeners) {
						try {
							l.keyReleased((KeyEvent)e.event,getScreen());
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
					}
					
					break;
				case 3:
					for(InputListener l : currentScreen.listeners) {
						try {
							l.mouseClicked((MouseEvent)e.event,getScreen());
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
					}
					
					break;
				case 4:
					for(InputListener l : currentScreen.listeners) {
						try {
							l.mouseEntered((MouseEvent)e.event,getScreen());
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
					}
					
					break;
				case 5:
					for(InputListener l : currentScreen.listeners) {
						try {
							l.mouseExited((MouseEvent)e.event,getScreen());
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
					}
					
					break;
				case 6:
					input.mousePressed((MouseEvent)e.event);
					
					for(InputListener l : currentScreen.listeners) {
						try {
							l.mousePressed((MouseEvent)e.event,getScreen());
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
					}
					
					break;
				case 7:
					input.mouseReleased((MouseEvent)e.event);
					for(InputListener l : currentScreen.listeners) {
						try {
							l.mouseReleased((MouseEvent)e.event,getScreen());
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
					}
					
					break;
				case 8:
					input.mouseDragged((MouseEvent)e.event);
					
					for(InputListener l : currentScreen.listeners) {
						try {
							l.mouseDragged((MouseEvent)e.event,getScreen());
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
					}
					
					break;
				case 9:
					input.mouseMoved((MouseEvent)e.event);
					
					for(InputListener l : currentScreen.listeners) {
						try {
							l.mouseMoved((MouseEvent)e.event,getScreen());
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
					}
					
					break;
				case 10:
					for(InputListener l : currentScreen.listeners) {
						try {
							l.mouseWheelMoved((MouseWheelEvent)e.event,getScreen());
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
					}
					
					break;
				case 11:
					try {
						resized(getWidth(),getHeight());
					}
					catch(Exception exc) {
						exc.printStackTrace();
					}
					
					break;
				case 12:
					try {
						focusGained();
					}
					catch(Exception exc) {
						exc.printStackTrace();
					}
					
					break;
				case 13:
					try {
						focusLost();
					}
					catch(Exception exc) {
						exc.printStackTrace();
					}
					
					break;
				case 14:
					try {
						setFullScreen(false);
					}
					catch(Exception exc) {
						exc.printStackTrace();
					}
			}
		}
		
		processingEvents = false;
		for(TempListener t : tempListeners) {
			if(t.isAdding)
				addInputListener(t.screenInfo, t.listener);
			else
				removeInputListener(t.screenInfo,t.listener);
		}
		
		tempListeners.clear();
		
		events.clear();
	}
	
	@Override
	public final void init() {
		setLayout(new BorderLayout());
		
		add(canvas);
		invalidate();
		validate();
		
		canvas.setSize(super.getWidth(),super.getHeight());
		canvas.setIgnoreRepaint(true);
		canvas.setFocusTraversalKeysEnabled(false);
		
		canvas.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent ce) {
				events.add(new Event(11,ce));
				
				if(isActive) {
					width = canvas.getWidth();
					height = canvas.getHeight();
				}
			}
		});
		
		canvas.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent fe) {
				events.add(new Event(12,fe));
			}
			
			@Override
			public void focusLost(FocusEvent fe) {
				events.add(new Event(13,fe));
				
				input.reset();
			}
		});
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					Thread.currentThread().setPriority(8);
				}
			});
		}
		catch(Exception exc) {}
	}
	
	/**
	 * Automatically called if this game is an Applet, otherwise it has to be manually called. This method starts the game loop thread.
	 */
	@Override
	public final void start() {
		if(!isActive())
			new Thread() {
				@Override
				public void run() {
					gameLoop();
				}
			}.start();
	}
	
	/**
	 * Called when the window is closed. Calling this method stops the game loop. stopGame() is then called on the game loop thread.
	 */
	@Override
	public final void stop() {
		sound.setOn(false);
		isActive = false;
	}
	
	/**
	 * Called as soon as the game is created.
	 */
	protected abstract void initGame();
	
	/**
	 * Called the set FPS times a second. Updates the current screen.
	 * @param deltaTime The time passed since the last call to it.
	 */
	protected void update(long deltaTime) {
		getScreen().update(deltaTime);
	}
	
	/**
	 * Called when this game loses focus.
	 */
	protected void paused() {
		getScreen().paused();
	}
	
	/**
	 * Called when this game regains focus.
	 */
	protected void resumed() {
		getScreen().resumed();
	}
	
	/**
	 * Called when the focus is gained.
	 */
	protected void focusGained() {}
	
	/**
	 * Called when the focus is lost.
	 */
	protected void focusLost() {}
	
	/**
	 * called when the game is resized.
	 * @param width The new width.
	 * @param height The new height.
	 */
	protected void resized(int width, int height) {
		getScreen().resized(width, height);
	}
	
	/**
	 * Called when this game is stopped.
	 * @return true if the window should be closed, false otherwise.
	 */
	protected boolean stopGame() {
		return true;
	}
	
	public final void paint(Graphics g) {}
	
	/**
	 * Called the set FPS times a second. Draws the current screen.
	 * @param g The Graphics context to be used to draw to the canvas.
	 */
	protected void paint(Graphics2D g) {
		getScreen().draw((Graphics2D)g.create());
	}
	
	/**
	 * Adds a screen to this game.
	 * @param screen The Screen to add.
	 * @param name The name of the screen.
	 */
	public void addScreen(String name, Screen screen) {
		if(screen == null)
			throw new IllegalArgumentException("Screen cannot be null.");
		if(name == null)
			throw new IllegalArgumentException("Name cannot be null.");
		
		screens.put(name,new ScreenInfo(screen));
		screen.init(this);
	}
	
	/**
	 * Returns the current screen.
	 * @return The current screen.
	 */
	public Screen getScreen() {
		return currentScreen.screen;
	}
	
	/**
	 * Returns the name of the current screen. This is the same as calling <code>getName(getScreen())</code>.
	 * @return The name of the current screen.
	 */
	public String getScreenName() {
		return getName(getScreen());
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
	
	public boolean isScreen(String name) {
		return screens.get(name) != null;
	}
	
	public boolean isScreen(Screen screen) {
		return getScreenInfo(screen) != null;
	}
	
	/**
	 * Adds the screen and sets it as the current screen.
	 * @param screen The Screen to be added and set.
	 * @param name The name assigned to the Screen.
	 */
	public void setScreen(String name, Screen screen) {
		addScreen(name,screen);
		setScreen(name);
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
		
		currentScreen.screen.hide();
		currentScreen = screenInfo;
		
		currentScreen.screen.show();
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
		
		addInputListener(info,listener);
	}
	
	private void addInputListener(ScreenInfo screenInfo, InputListener listener) {
		if(screenInfo == null || !screens.containsValue(screenInfo))
			throw new IllegalArgumentException("Screen has not been added.");
		
		if(listener == null)
			throw new IllegalArgumentException("InputListener cannot be null.");
		
		if(processingEvents)
			tempListeners.add(new TempListener(screenInfo,listener,true));
		else
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
	
	private void removeInputListener(ScreenInfo screenInfo, InputListener listener) {
		if(screenInfo == null || !screens.containsValue(screenInfo))
			throw new IllegalArgumentException("Screen has not been added.");
		
		if(listener == null)
			throw new IllegalArgumentException("InputListener cannot be null.");
		
		if(processingEvents)
			tempListeners.add(new TempListener(screenInfo,listener,false));
		else
			screenInfo.listeners.remove(listener);
	}
	
	public void addCallback(long delay, Runnable r) {
		if(r == null)
			throw new NullPointerException("Runnable is null");
		
		callbacks.add(new Callback(r,delay));
	}
	
	public boolean removeCallback(Runnable r) {
		return callbacks.remove(r);
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
	
	public void setMaximumUpdatesBeforeRender(int count) {
		maxUpdates = count;
	}
	
	public int getMaximumUpdatesBeforeRender() {
		return maxUpdates;
	}
	
	/**
	 * Returns the optimal FPS of this game.
	 * @return The number of udpates and frames shown per second.
	 */
	public int getFPS() {
		return FPS;
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
	
	private static class Event {
		int id;
		AWTEvent event;
		
		public Event(int id, AWTEvent event) {
			this.id = id;
			this.event = event;
		}
	}
	
	private static class TempListener {
		private ScreenInfo screenInfo;
		private InputListener listener;
		private boolean isAdding;
		
		public TempListener(ScreenInfo screenInfo, InputListener listener, boolean isAdding) {
			this.screenInfo = screenInfo;
			this.listener = listener;
			this.isAdding = isAdding;
		}
	}
	
	private static class Callback {
		private final Runnable r;
		private final long delay;
		
		private long lastTime;
		
		public Callback(Runnable r, long delay) {
			this.r = r;
			this.delay = delay;
			lastTime = System.nanoTime();
		}
	}
	
	private class Listener implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
		@Override
		public void keyTyped(KeyEvent key) {
			events.add(new Event(0,key));
		}
		
		@Override
		public void keyPressed(KeyEvent key) {
			events.add(new Event(1,key));
		}
		
		@Override
		public void keyReleased(KeyEvent key) {
			events.add(new Event(2,key));
		}
		
		@Override
		public void mouseClicked(MouseEvent me) {
			events.add(new Event(3,me));
		}
		
		@Override
		public void mouseEntered(MouseEvent me) {
			events.add(new Event(4,me));
		}
		
		@Override
		public void mouseExited(MouseEvent me) {
			events.add(new Event(5,me));
		}
		
		@Override
		public void mousePressed(MouseEvent me) {
			events.add(new Event(6,me));
		}
		
		@Override
		public void mouseReleased(MouseEvent me) {
			events.add(new Event(7,me));
		}
		
		@Override
		public void mouseDragged(MouseEvent me) {
			events.add(new Event(8,me));
		}
		
		@Override
		public void mouseMoved(MouseEvent me) {
			events.add(new Event(9,me));
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent mwe) {
			events.add(new Event(10,mwe));
		}
	}
}
