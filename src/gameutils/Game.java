package gameutils;

import gameutils.util.FastMath;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//Update the game<br>
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
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//Draw the game<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * }
 * </code>
 * @author Roi Atalla
 */
public abstract class Game extends Applet implements Runnable {
	private static final long serialVersionUID = -1870725768768871166L;
	
	/**
	 * Initializes and displays the window.
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
	 * 1 second in nanoseconds in double precision, AKA 1,000,000 nanoseconds.
	 */
	public static final double ONE_SECOND = 1e9;
	
	/**
	 * 1 second in nanoseconds as a long, AKA 1,000,000 nanoseconds.
	 */
	public static final long ONE_SECOND_L = (long)ONE_SECOND;
	
	private final Art art;
	private final Sound sound;
	private final Map<String,ScreenInfo> screens;
	private ScreenInfo screenInfo;
	private final Canvas canvas;
	private final Input input;
	private Object quality;
	private int maxUpdates;
	private int FPS;
	private double version;
	private boolean showFPS;
	private boolean useYield;
	private boolean standardKeysEnabled = true;
	private boolean isApplet = true;
	private volatile boolean isActive;
	private volatile boolean isPaused;
	
	/**
	 * Default constructor. The defaults are:<br>
	 * - MAX_UPDATES is set
	 * - 60FPS<br>
	 * - Version 1.0<br>
	 * - showFPS = true<br>
	 * - quality = high<br>
	 * - standardKeysEnabled = true
	 */
	public Game() {
		this(60,1.0);
	}
	
	/**
	 * Sets defaults and the FPS and version.
	 * @param FPS The FPS to achieve.
	 * @param version The version of this game.
	 */
	public Game(int FPS, double version) {
		art = new Art();
		sound = new Sound();
		
		screens = Collections.synchronizedMap(new HashMap<String,ScreenInfo>());
		
		screenInfo = new ScreenInfo(new Screen() {
			public void init(Game game) {}
			public Game getParent() { return null; }
			public void show() {}
			public void hide() {}
			public void update(long deltaTime) {}
			public void draw(Graphics2D g) {}
		});
		
		canvas = new Canvas();
		input = new Input(canvas);
		
		this.FPS = FPS;
		this.version = version;
		showFPS = true;
		
		quality = RenderingHints.VALUE_ANTIALIAS_ON;
		
		maxUpdates = MAX_UPDATES;
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
		if(isActive()) {
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
		if(isApplet())
			super.resize(width,height);
		else
			setSize(width,height);
	}
	
	/**
	 * If this game is an Applet, it calls the superclass's resize method, else it adjusts the JFrame according to the platform specific Insets.
	 * @param width The new width of this game's canvas
	 * @param height The new height of this game's canvas
	 */
	public void setSize(int width, int height) {
		if(isApplet())
			super.resize(width,height);
		else {
			Insets i = getRootParent().getInsets();
			getRootParent().setSize(width+i.right+i.left,height+i.bottom+i.top);
			((JFrame)getRootParent()).setLocationRelativeTo(null);
		}
	}
	
	/**
	 * Game loop.
	 */
	public final void run() {
		if(isActive())
			return;
		
		Thread.currentThread().setName("Game Loop Thread");
		
		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch(Exception exc) {}
		
		synchronized(Game.this) {
			initGame();
		}
		
		Listener listener = new Listener();
		canvas.addKeyListener(listener);
		canvas.addMouseListener(listener);
		canvas.addMouseMotionListener(listener);
		canvas.addMouseWheelListener(listener);
		
		BufferStrategy strategy = canvas.getBufferStrategy();
		
		int frames = 0;
		int currentFPS = 0;
		long time = System.nanoTime();
		long lastTime = System.nanoTime();
		
		isActive = true;
		
		canvas.requestFocus();
		
		while(isActive()) {
			long now = System.nanoTime();
			long diffTime = now-lastTime;
			lastTime = now;
			
			int updateCount = 0;
			
			while(diffTime > 0 && (maxUpdates == MAX_UPDATES || updateCount < maxUpdates) && !isPaused()) {
				long deltaTime;
				
				if(FPS > 0)
					deltaTime = Math.min(diffTime,ONE_SECOND_L/FPS);
				else
					deltaTime = diffTime;
				
				try{
					synchronized(Game.this) {
						update(deltaTime);
					}
				}
				catch(Exception exc) {
					exc.printStackTrace();
				}
				
				diffTime -= deltaTime;
				
				updateCount++;
			}
			
			try{
				do{
					do{
						Graphics2D g = (Graphics2D)strategy.getDrawGraphics();
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,quality);
						
						try{
							synchronized(Game.this) {
								paint(g);
							}
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
						
						if(showFPS) {
							g.setColor(Color.black);
							g.setFont(new Font(Font.SANS_SERIF,Font.TRUETYPE_FONT,10));
							g.drawString("Version " + version + "    " + currentFPS + " FPS",2,canvas.getHeight()-2);
						}
						
						g.dispose();
					}while(strategy.contentsRestored());
					
					strategy.show();
				}while(strategy.contentsLost());
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
			
			if(System.nanoTime()-time >= ONE_SECOND_L) {
				time = System.nanoTime();
				currentFPS = frames;
				frames = 0;
			}
			
			frames++;
			
			try{
				if(FPS > 0) {
					long sleepTime = FastMath.round((ONE_SECOND/FPS)-(System.nanoTime()-lastTime));
					if(sleepTime <= 0)
						continue;
					
					long prevTime = System.nanoTime();
					while(System.nanoTime()-prevTime <= sleepTime) {
						if(useYield)
							Thread.yield();
						else
							Thread.sleep(1);
					}
				}
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
		}
		
		stopGame();
	}
	
	public final void init() {
		setIgnoreRepaint(true);
		setLayout(new BorderLayout());
		
		add(canvas);
		canvas.setIgnoreRepaint(true);
		canvas.createBufferStrategy(2);
		
		canvas.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				resized(getWidth(),getHeight());
			}
		});
		
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
	 * Automatically called if this game is an Applet, otherwise it has to be manually called.
	 */
	public final void start() {
		if(!isActive())
			new Thread(this).start();
	}
	
	/**
	 * Called when this game is stopped. Calling this method stops the game loop. stopGame() is then called.
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
		screenInfo.screen.update(deltaTime);
	}
	
	/**
	 * Called when this game loses focus.
	 */
	protected abstract void paused();
	
	/**
	 * Called when this game regains focus.
	 */
	protected abstract void resumed();
	
	/**
	 * called when the game is resized.
	 * @param width The new width.
	 * @param height The new height.
	 */
	protected abstract void resized(int width, int height);
	
	/**
	 * Called when this game is stopped.
	 */
	protected abstract void stopGame();
	
	/**
	 * Called the set FPS times a second. Clears the window using the Graphics2D's background color then draws the current screen.
	 * @param g The Graphics context to be used to draw to the canvas.
	 */
	protected void paint(Graphics2D g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		screenInfo.screen.draw((Graphics2D)g.create());
	}
	
	/**
	 * Adds a screen to this game.
	 * @param screen The Screen to add.
	 * @param name The name of the screen.
	 */
	public synchronized void addScreen(Screen screen, String name) {
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
		return screenInfo.screen;
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
	public synchronized void setScreen(Screen screen, String name) {
		addScreen(screen,name);
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
	 * Sets whether the game loop should use Thread.yield() or Thread.sleep(1).<br>
	 * Thread.yield() produces a smoother game loop but at the expense of a high CPU usage.<br>
	 * Thread.sleep(1) is less smooth but barely uses any CPU time.<br>
	 * The default is Thread.sleep(1).
	 * @param useYield If true, uses Thread.yield(), otherwise uses Thread.sleep(1).
	 */
	public void useYield(boolean useYield) {
		this.useYield = useYield;
	}
	
	/**
	 * Returns whether the game loop uses Thread.yield() or Thread.sleep(1). The default is Thread.sleep(1).
	 * @return True if the game loop uses Thread.yield(), false if it uses Thread.sleep(1).
	 */
	public boolean usesYield() {
		return useYield;
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
	 * Sets the maximum number of updates before render.
	 * @param maxUpdates The maximum number of updates before render.
	 */
	public void setMaximumUpdatesBeforeRender(int maxUpdates) {
		this.maxUpdates = maxUpdates;
	}
	
	/**
	 * Returns the maximum number of updates before render.
	 * @return The maximum number of updates before render.
	 */
	public int getMaximumUpdatesBeforeRender() {
		return maxUpdates;
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
				l.keyTyped(key,getScreen());
		}
		
		public void keyPressed(KeyEvent key) {
			for(InputListener l : screenInfo.listeners)
				l.keyPressed(key,getScreen());
			
			if(isStandardKeysEnabled()) {
				switch(key.getKeyCode()) {
					case KeyEvent.VK_P: if(isPaused()) resume(); else pause(); break;
					case KeyEvent.VK_M: sound.setOn(!sound.isOn()); break;
					case KeyEvent.VK_Q: setHighQuality(!isHighQuality()); break;
				}
			}
		}
		
		public void keyReleased(KeyEvent key) {
			for(InputListener l : screenInfo.listeners)
				l.keyReleased(key,getScreen());
		}
		
		public void mouseClicked(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mouseClicked(me,getScreen());
		}
		
		public void mouseEntered(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mouseEntered(me,getScreen());
		}
		
		public void mouseExited(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mouseExited(me,getScreen());
		}
		
		public void mousePressed(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mousePressed(me,getScreen());
		}
		
		public void mouseReleased(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mouseReleased(me,getScreen());
		}
		
		public void mouseDragged(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mouseDragged(me,getScreen());
		}
		
		public void mouseMoved(MouseEvent me) {
			for(InputListener l : screenInfo.listeners)
				l.mouseMoved(me,getScreen());
		}
		
		public void mouseWheelMoved(MouseWheelEvent mwe) {
			for(InputListener l : screenInfo.listeners)
				l.mouseWheelMoved(mwe,getScreen());
		}
	}
}