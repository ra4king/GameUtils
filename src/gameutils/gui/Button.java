package gameutils.gui;

import gameutils.AbstractInputListener;
import gameutils.Screen;
import gameutils.util.FastMath;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * A Button extends Widget and draws a button.
 * @author Roi Atalla
 */
public class Button extends Widget {
	private Action action;
	private RoundRectangle2D.Double bounds;
	private Font font;
	private String text;
	private Color textColor;
	private GradientPaint background;
	private GradientPaint bgHighlight;
	private GradientPaint bgPressed;
	private Color border;
	private Color borderHighlight;
	private Color borderPressed;
	private Color disabled;
	private int textX, textY;
	private int centerX, centerY;
	private int arcwidth, archeight;
	private boolean isCentered;
	private boolean isHighlighted;
	private boolean isPressed;
	private boolean isEnabled;
	
	/**
	 * Initializes this Object. The defaults are:<br>
	 * - text color = black<br>
	 * - background = transparent,<br>
	 * - background highlight = half transparent white<br>
	 * - background pressed = half transparent light gray<br>
	 * - border = black<br>
	 * - border highlight = orange<br>
	 * - border pressed = dark gray,<br>
	 * - disabled color = half transparent dark gray.<br>
	 * 
	 * @param text The text to show on the button.
	 * @param fontSize The font size of the text. The default font is Bold Sans-Serif.
	 * @param x The X position of the button.
	 * @param y The Y position of the button.
	 * @param arcwidth The arc width of each corner of the button.
	 * @param archeight The arc height of each corner of the button.
	 * @param centered If true, the X and Y are the center of the text, else they are the top left corner of the text.
	 * @param action The action to be called when this button is pressed.
	 */
	public Button(String text, int fontSize, int x, int y, int arcwidth, int archeight, boolean centered, Action action) {
		this.action = action;
		this.text = text;
		
		font = new Font(Font.SANS_SERIF,Font.BOLD,fontSize);
		
		centerX = x;
		centerY = y;
		this.arcwidth = arcwidth;
		this.archeight = archeight;
		
		isCentered = centered;
		
		recalcCoords();
		
		textColor = Color.black;
		setBackground(new Color(0,0,0,0));
		setBackgroundHighlight(new Color(255,255,255,100));
		setBackgroundPressed(new Color(128,128,128,200));
		border = Color.black;
		borderHighlight = Color.orange;
		borderPressed = Color.darkGray;
		disabled = new Color(192,192,192,100);
		
		isEnabled = true;
	}
	
	public void init(Screen screen) {
		super.init(screen);
		
		screen.getParent().addInputListener(screen, new AbstractInputListener() {
			public void mousePressed(MouseEvent me) {
				if(me.getButton() != MouseEvent.BUTTON1)
					return;
				
				setPressed(false);
				setHighlighted(false);
				
				if(getButtonBounds().contains(me.getPoint()) && isEnabled())
					setPressed(true);
			}
			
			public void mouseReleased(MouseEvent me) {
				if(me.getButton() != MouseEvent.BUTTON1)
					return;
				
				setHighlighted(false);
				
				if(getButtonBounds().contains(me.getPoint()) && isEnabled())
					if(isPressed())
						getAction().doAction(Button.this);
					else
						setHighlighted(true);
				
				setPressed(false);
			}
			
			public void mouseMoved(MouseEvent me) {
				setHighlighted(false);
				
				if(getButtonBounds().contains(me.getPoint()) && isEnabled() && !isPressed())
					setHighlighted(true);
			}
		});
	}
	
	/**
	 * Returns the text of this button.
	 * @return The text of this button.
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Sets the text of this button.
	 * @param text The new text of this button.
	 */
	public void setText(String text) {
		this.text = text;
		
		recalcCoords();
	}
	
	/**
	 * Returns the font of the text.
	 * @return The font of the text.
	 */
	public Font getFont() {
		return font;
	}
	
	/**
	 * Sets the font of the text.
	 * @param font The new font of the text.
	 */
	public void setFont(Font font) {
		if(font != null)
			this.font = font;
		
		recalcCoords();
	}
	
	private void recalcCoords() {
		Graphics2D g = (Graphics2D)new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB).getGraphics();
		FontMetrics fm = g.getFontMetrics(font);
		int width = fm.stringWidth(text);
		super.setWidth((int)(width+40));
		super.setHeight((int)(fm.getHeight()+10));
		if(isCentered) {
			super.setX(centerX-getWidth()/2);
			super.setY(centerY-getHeight()/2);
		}
		else {
			super.setX(centerX);
			super.setY(centerY);
		}
		textX = (int)FastMath.round(getX()+(getWidth()-width)/2);
		textY = (int)FastMath.round(getY()+getHeight()/2+fm.getHeight()/2-fm.getDescent());
	}
	
	/**
	 * Calls MenuItem's setX method and recalculates all the coordinates.
	 */
	public void setX(int x) {
		super.setX(x);
		recalcCoords();
	}
	
	/**
	 * Calls MenuItem's setY method and recalculates all the coordinates.
	 */
	public void setY(int y) {
		super.setY(y);
		recalcCoords();
	}
	
	/**
	 * Calls MenuItem's setWidth method and recalculates all the coordinates.
	 */
	public void setWidth(int width) {
		super.setWidth(width);
		recalcCoords();
	}
	
	/**
	 * Calls MenuItem's setHeight method and recalculates all the coordinates.
	 */
	public void setHeight(int height) {
		super.setHeight(height);
		recalcCoords();
	}
	
	/**
	 * Returns the action called when this button is pressed.
	 * @return The action called when this button is pressed.
	 */
	public Action getAction() {
		return action;
	}
	
	/**
	 * Sets the action to be called when this button is pressed.
	 * @param action The new action to be called when this button is pressed.
	 */
	public void setAction(Action action) {
		this.action = action;
	}
	
	/**
	 * Returns this button's bounds.
	 * @return This button's bounds.
	 */
	public RoundRectangle2D.Double getButtonBounds() {
		if(bounds == null)
			bounds = new RoundRectangle2D.Double();
		bounds.setRoundRect(getX(),getY(),getWidth(),getHeight(),arcwidth,archeight);
		return bounds;
	}
	
	/**
	 * Sets the highlighted state of the button.
	 * @param highlighted If true, this button is highlighted, else this button is normal.
	 */
	public void setHighlighted(boolean highlighted) {
		isHighlighted = highlighted;
	}
	
	/**
	 * Returns the highlighted state of the button.
	 * @return True if this button is highlighted, false otherwise.
	 */
	public boolean isHighlighted() {
		return isHighlighted;
	}
	
	/**
	 * Sets the pressed state of the button.
	 * @param pressed If true, this button is pressed, else it is not.
	 */
	public void setPressed(boolean pressed) {
		isPressed = pressed;
	}
	
	/**
	 * Returns the pressed state of the button.
	 * @return True if this button is pressed, false otherwise.
	 */
	public boolean isPressed() {
		return isPressed;
	}
	
	/**
	 * Sets the enabled state of the button.
	 * @param enabled If true, this button is enabled, else it is disabled.
	 */
	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
	}
	
	/**
	 * Returns the enabled state of the button.
	 * @return True if this button is enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return isEnabled;
	}
	
	/**
	 * Sets the color of the text.
	 * @param color The new color of the text.
	 */
	public void setColor(Color color) {
		this.textColor = color;
	}
	
	/**
	 * Sets the background color of the button.
	 * @param background The new background color of the button.
	 */
	public void setBackground(Color background) {
		this.background = new GradientPaint((float)(getX()+getWidth()/2.0),(float)getY(),Color.white,(float)(getX()+getWidth()/2.0),(float)(getY()+getHeight()),background);
		setBackgroundPressed(background.darker());
	}
	
	/**
	 * Sets the background color of the button when it is highlighted.
	 * @param highlight The new background highlight color of the button.
	 */
	public void setBackgroundHighlight(Color highlight) {
		bgHighlight = new GradientPaint((float)(getX()+getWidth()/2.0),(float)getY(),Color.white,(float)(getX()+getWidth()/2.0),(float)(getY()+getHeight()),highlight);
	}
	
	/**
	 * Sets the background color of the button when it is pressed.
	 * @param pressed The new background pressed color of the button.
	 */
	public void setBackgroundPressed(Color pressed) {
		bgPressed = new GradientPaint((float)(getX()+getWidth()/2.0),(float)getY(),pressed,(float)(getX()+getWidth()/2.0),(float)(getY()+getHeight()),Color.white);
	}
	
	/**
	 * Sets the border color of the button.
	 * @param border The new border color of the button.
	 */
	public void setBorder(Color border) {
		this.border = border;
	}
	
	/**
	 * Sets the border color of the button when it is highlighted.
	 * @param highlight The new border highlight color of the button.
	 */
	public void setBorderHighlight(Color highlight) {
		borderHighlight = highlight;
	}
	
	/**
	 * Sets the border color of the button when it is pressed.
	 * @param pressed The new border pressed color of the button.
	 */
	public void setBorderPressed(Color pressed) {
		borderPressed = pressed;
	}
	
	public void update(long deltaTime) {}
	
	/**
	 * Draws the button.
	 */
	public void draw(Graphics2D g) {
		g.setFont(font);
		
		if(!isEnabled) {
			g.setPaint(background);
			g.fill(getButtonBounds());
			
			g.setColor(border);
			g.draw(getButtonBounds());
			
			g.setColor(textColor);
			g.drawString(text,textX,textY);
			
			g.setColor(disabled);
			g.fill(getButtonBounds());
		}
		else if(isPressed) {
			g.setPaint(bgPressed);
			g.fill(getButtonBounds());
			
			g.setColor(borderPressed);
			g.draw(getButtonBounds());
			
			g.setColor(textColor);
			g.drawString(text,textX,textY);
		}
		else if(isHighlighted) {
			g.setPaint(background);
			g.fill(getButtonBounds());
			
			g.setPaint(bgHighlight);
			g.fill(getButtonBounds());
			
			g.setColor(borderHighlight);
			g.draw(getButtonBounds());
			
			g.setColor(textColor);
			g.drawString(text,textX,textY);
		}
		else {
			g.setPaint(background);
			g.fill(getButtonBounds());
			
			g.setColor(border);
			g.draw(getButtonBounds());
			
			g.setColor(textColor);
			g.drawString(text,textX,textY);
		}
	}
	
	/**
	 * This interface is in use mainly but the Button class.
	 * @author Roi Atalla
	 */
	public static interface Action {
		/**
		 * Called when an action has occurred.
		 * @param button The Button where an action has occurred.
		 */
		public void doAction(Button button);
	}
}