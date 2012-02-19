package com.ra4king.gameutils;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
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
 * &nbsp;&nbsp;&nbsp;&nbsp;public static void main(String args[]) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MyGame game = new MyGame();<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;game.setupFrame("My Game",800,600,true);<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;game.start();<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;public void initGame() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//Initialize the game<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;public void update(long deltaTime) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;super.update(deltaTime);<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//Optional: update the game<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;public void paused() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//Pause the game<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 *<br>     
 * &nbsp;&nbsp;&nbsp;&nbsp;public void resumed() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//Resume the game<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 *<br>     
 * &nbsp;&nbsp;&nbsp;&nbsp;public void resized(int width, int height) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//Resize the game<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 *<br>     
 * &nbsp;&nbsp;&nbsp;&nbsp;public void stopGame() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//Stop the game<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 *<br>     
 * &nbsp;&nbsp;&nbsp;&nbsp;public void paint(Graphics2D g) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;super.paint(g);<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//Optional: draw the game<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
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
	}
	
	/**
	 * Initializes and displays the window.
	 * @param title The title of the window
	 * @param width The width of the window
	 * @param height The height of the window
	 * @param resizable If true, the window will be resizable, else it will not be resizable.
	 * @return The JFrame that was initialized by this method.
	 */
	protected final JFrame setupFrame(String title, boolean resizable) {
		final JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setIgnoreRepaint(true);
		frame.setResizable(resizable);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				stop();
			}
		});
		
		frame.add(this);
		frame.setVisible(true);
		
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
	
	/**
	 * Used to notify the game loop thread to update the maximum number of times before render.
	 */
	public static final int MAX_UPDATES = -1;
	
	/**
	 * 1 second in nanoseconds, AKA 1,000,000 nanoseconds.
	 */
	public static final long ONE_SECOND = (long)1e9;
	
	private final Art art;
	private final Sound sound;
	
	private final Map<String,ScreenInfo> screens;
	private ScreenInfo currentScreen;
	
	private final Canvas canvas;
	private BufferStrategy strategy;
	
	private final Input input;
	private ArrayList<Event> events;
	private ArrayList<Event> tempEvents;
	
	private boolean isApplet = true;
	private int width, height;
	
	private int FPS;
	private double version;
	private boolean showFPS;
	private volatile boolean isProcessingEvents;
	private volatile boolean isActive;
	private volatile boolean isPaused;
	
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
		
		screens = Collections.synchronizedMap(new HashMap<String,ScreenInfo>());
		
		currentScreen = new ScreenInfo(new Screen() {
			public void init(Game game) {}
			public Game getGame() { return Game.this; }
			public void show() {}
			public void hide() {}
			public void paused() {}
			public void resumed() {}
			public void resized(int width, int height) {}
			public void update(long deltaTime) {}
			public void draw(Graphics2D g) {}
		});
		
		screens.put("Default", currentScreen);
		
		canvas = new Canvas();
		input = new Input();
		
		events = new ArrayList<Event>();
		tempEvents = new ArrayList<Event>();
		
		this.width = width;
		this.height = height;
		setFPS(FPS);
		setVersion(version);
		
		showFPS = true;
		
		if(System.getProperty("os.name").startsWith("Win")) {
			new Thread() {
				{
					setDaemon(true);
					start();
				}
				
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
	 * Returns the current working directory of this game.
	 * @return The current working directory of this game.
	 */
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
		return getParent().getParent().getParent().getParent();
	}
	
	public int getWidth() {
		return width;
	}
	
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
	public void resize(int width, int height) {
		setSize(width,height);
	}
	
	/**
	 * If this game is an Applet, it calls the superclass's resize method, else it adjusts the JFrame according to the platform specific Insets.
	 * @param width The new width of this game's canvas
	 * @param height The new height of this game's canvas
	 */
	public void setSize(int width, int height) {
		invalidate();
		
		if(isApplet())
			super.resize(width,height);
		else {
			Insets i = getRootParent().getInsets();
			getRootParent().setSize(width+i.right+i.left,height+i.bottom+i.top);
			((JFrame)getRootParent()).setLocationRelativeTo(null);
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
			
			JFrame frame = new JFrame();
			frame.setResizable(false);
			frame.setUndecorated(true);
			frame.setIgnoreRepaint(true);
			
			remove(canvas);
			invalidate();
			validate();
			
			frame.add(canvas);
			
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					synchronized(Game.this) {
						setFullScreen(false);
					}
				}
			});
			
			if(!isApplet())
				getRootParent().setVisible(false);
			
			gd.setFullScreenWindow(frame);
			
			canvas.createBufferStrategy(3);
			strategy = canvas.getBufferStrategy();
		}
		else {
			if(!isFullScreen())
				return;
			
			gd.getFullScreenWindow().remove(canvas);
			gd.getFullScreenWindow().dispose();
			gd.setFullScreenWindow(null);
			add(canvas);
			invalidate();
			validate();
			
			if(!isApplet())
				getRootParent().setVisible(true);
			
			canvas.createBufferStrategy(3);
			strategy = canvas.getBufferStrategy();
		}
		
		canvas.requestFocus();
		
		width = canvas.getWidth();
		height = canvas.getHeight();
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
			
			synchronized(Game.this) {
				initGame();
			}
			
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
			canvas.createBufferStrategy(3);
			strategy = canvas.getBufferStrategy();
		}
		
		Font fpsFont = new Font(Font.SANS_SERIF,Font.TRUETYPE_FONT,10);
		
		int frames = 0;
		int currentFPS = 0;
		long time = System.nanoTime();
		long lastTime = System.nanoTime();
		
		isActive = true;
		
		canvas.requestFocus();
		
		while(isActive()) {
			try{
				processEvents();
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
			
			long diffTime = System.nanoTime()-lastTime;
			
			if(!isPaused && (FPS <= 0 || diffTime >= ONE_SECOND/FPS)) {
				update(diffTime);
				
				lastTime += diffTime;
			}
			
			try{
				do{
					do{
						Graphics2D g = (Graphics2D)strategy.getDrawGraphics();
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
						
						try{
							synchronized(Game.this) {
								paint(g);
							}
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
			
			Thread.yield();
		}
		
		if(stopGame() && !isApplet())
			System.exit(0);
	}
	
	private void processEvents() {
		isProcessingEvents = true;
		
		synchronized(events) {
			for(Event e : events) {
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
				}
			}
			
			events.clear();
			events.addAll(tempEvents);
			tempEvents.clear();
		}
		
		isProcessingEvents = false;
	}
	
	public final void init() {
		setLayout(new BorderLayout());
		
		add(canvas);
		canvas.setSize(super.getWidth(),super.getHeight());
		canvas.setIgnoreRepaint(true);
		canvas.setFocusTraversalKeysEnabled(false);
		
		canvas.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				if(isProcessingEvents) {
					tempEvents.add(new Event(11,ce));
				}
				else {
					synchronized(events) {
						events.add(new Event(11,ce));
					}
				}
				
				if(isActive) {
					width = canvas.getWidth();
					height = canvas.getHeight();
				}
			}
		});
		
		canvas.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent fe) {
				if(isProcessingEvents) {
					tempEvents.add(new Event(12,fe));
				}
				else {
					synchronized(events) {
						events.add(new Event(12,fe));
					}
				}
			}
			
			public void focusLost(FocusEvent fe) {
				if(isProcessingEvents) {
					tempEvents.add(new Event(13,fe));
				}
				else {
					synchronized(events) {
						events.add(new Event(13,fe));
					}
				}
				
				input.reset();
			}
		});
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
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
	public final void start() {
		if(!isActive())
			new Thread() {
				public void run() {
					gameLoop();
				}
			}.start();
	}
	
	/**
	 * Called when the window is closed. Calling this method stops the game loop. stopGame() is then called on the game loop thread.
	 */
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
	
	/**
	 * Called the set FPS times a second. Clears the window using the Graphics2D's background color then draws the current screen.
	 * @param g The Graphics context to be used to draw to the canvas.
	 */
	protected void paint(Graphics2D g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		getScreen().draw((Graphics2D)g.create());
	}
	
	/**
	 * Adds a screen to this game.
	 * @param screen The Screen to add.
	 * @param name The name of the screen.
	 */
	public synchronized void addScreen(String name, Screen screen) {
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
	public synchronized String getName(Screen screen) {
		for(String s : screens.keySet())
			if(screens.get(s).screen == screen)
				return s;
		return null;
	}
	
	/**
	 * Adds the screen and sets it as the current screen.
	 * @param screen The Screen to be added and set.
	 * @param name The name assigned to the Screen.
	 */
	public synchronized void setScreen(String name, Screen screen) {
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
		
		if(isActive)
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
	
	private class Listener implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
		public void keyTyped(KeyEvent key) {
			if(isProcessingEvents) {
				tempEvents.add(new Event(0,key));
			}
			else {
				synchronized(events) {
					events.add(new Event(0,key));
				}
			}
		}
		
		public void keyPressed(KeyEvent key) {
			if(isProcessingEvents) {
				tempEvents.add(new Event(1,key));
			}
			else {
				synchronized(events) {
					events.add(new Event(1,key));
				}
			}
		}
		
		public void keyReleased(KeyEvent key) {
			if(isProcessingEvents) {
				tempEvents.add(new Event(2,key));
			}
			else {
				synchronized(events) {
					events.add(new Event(2,key));
				}
			}
		}
		
		public void mouseClicked(MouseEvent me) {
			if(isProcessingEvents) {
				tempEvents.add(new Event(3,me));
			}
			else {
				synchronized(events) {
					events.add(new Event(3,me));
				}
			}
		}
		
		public void mouseEntered(MouseEvent me) {
			if(isProcessingEvents) {
				tempEvents.add(new Event(4,me));
			}
			else {
				synchronized(events) {
					events.add(new Event(4,me));
				}
			}
		}
		
		public void mouseExited(MouseEvent me) {
			if(isProcessingEvents) {
				tempEvents.add(new Event(5,me));
			}
			else {
				synchronized(events) {
					events.add(new Event(5,me));
				}
			}
		}
		
		public void mousePressed(MouseEvent me) {
			if(isProcessingEvents) {
				tempEvents.add(new Event(6,me));
			}
			else {
				synchronized(events) {
					events.add(new Event(6,me));
				}
			}
		}
		
		public void mouseReleased(MouseEvent me) {
			if(isProcessingEvents) {
				tempEvents.add(new Event(7,me));
			}
			else {
				synchronized(events) {
					events.add(new Event(7,me));
				}
			}
		}
		
		public void mouseDragged(MouseEvent me) {
			if(isProcessingEvents) {
				tempEvents.add(new Event(8,me));
			}
			else {
				synchronized(events) {
					events.add(new Event(8,me));
				}
			}
		}
		
		public void mouseMoved(MouseEvent me) {
			if(isProcessingEvents) {
				tempEvents.add(new Event(9,me));
			}
			else {
				synchronized(events) {
					events.add(new Event(9,me));
				}
			}
		}
		
		public void mouseWheelMoved(MouseWheelEvent mwe) {
			if(isProcessingEvents) {
				tempEvents.add(new Event(10,mwe));
			}
			else {
				synchronized(events) {
					events.add(new Event(10,mwe));
				}
			}
		}
	}
}