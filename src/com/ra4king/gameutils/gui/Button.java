package com.ra4king.gameutils.gui;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * A Button extends Widget and draws a button. If you are confused with the usage of Paint, remember Color implements Paint so you can still a Color if you wish.
 * @author Roi Atalla
 */
public class Button extends Widget {
	private Action action;
	private RoundRectangle2D.Double bounds;
	private Font font;
	private String text;
	private Paint textPaint;
	private Paint background, backgroundHighlight, backgroundPressed;
	private Paint border, borderHighlight, borderPressed;
	private Paint disabledBackground, disabledBorder;
	private double textX, textY, centerX, centerY;
	private int arcwidth, archeight;
	private boolean isCentered;
	private boolean isHighlighted;
	private boolean isPressed;
	private boolean isEnabled;
	private boolean useTextWidth, useTextHeight;
	
	/**
	 * Initializes this Button. The defaults are:<br>
	 * - text = black<br>
	 * - background = transparent,<br>
	 * - background highlight = half transparent white<br>
	 * - background pressed = half transparent light gray<br>
	 * - border = black<br>
	 * - border highlight = orange<br>
	 * - border pressed = dark gray,<br>
	 * - disabled = half transparent dark gray.<br>
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
	public Button(String text, int fontSize, double x, double y, int arcwidth, int archeight, boolean centered, Action action) {
		setFocusable(false);
		
		this.action = action;
		this.text = text;
		
		font = new Font(Font.SANS_SERIF,Font.BOLD,fontSize);
		
		centerX = x;
		centerY = y;
		this.arcwidth = arcwidth;
		this.archeight = archeight;
		
		isCentered = centered;
		
		useTextWidth = useTextHeight = true;
		
		recalcCoords();
		
		textPaint = Color.black;
		setBackgroundGradient(new Color(0,0,0,0));
		setBackgroundHighlightGradient(new Color(255,255,255,100));
		setBackgroundPressedGradient(new Color(128,128,128,200));
		border = Color.black;
		borderHighlight = Color.orange;
		borderPressed = Color.darkGray;
		disabledBackground = new Color(192,192,192,100);
		disabledBorder = Color.black;
		
		isEnabled = true;
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
		
		if(useTextWidth)
			super.setWidth(width+40);
		if(useTextHeight)
			super.setHeight(fm.getHeight()+10);
		
		if(isCentered) {
			super.setX(centerX-getWidth()/2);
			super.setY(centerY-getHeight()/2);
		}
		else {
			super.setX(centerX);
			super.setY(centerY);
		}
		
		textX = (int)Math.round(getX()+(getWidth()-width)/2);
		textY = (int)Math.round(getY()+getHeight()/2+fm.getHeight()/2-fm.getDescent());
	}
	
	/**
	 * Calls MenuItem's setX method and recalculates all the coordinates.
	 */
	@Override
	public void setX(double x) {
		centerX = x;
		recalcCoords();
	}
	
	/**
	 * Calls MenuItem's setY method and recalculates all the coordinates.
	 */
	@Override
	public void setY(double y) {
		centerY = y;
		recalcCoords();
	}
	
	/**
	 * Calls MenuItem's setWidth method and recalculates all the coordinates. This also sets useTextWidth to false.
	 */
	public void setWidth(int width) {
		super.setWidth(width);
		useTextWidth(false);
		recalcCoords();
	}
	
	/**
	 * Calls MenuItem's setHeight method and recalculates all the coordinates. This also sets useTextHeight to false.
	 */
	public void setHeight(int height) {
		super.setHeight(height);
		useTextHeight(false);
		recalcCoords();
	}
	
	public boolean isUsingTextWidth() {
		return useTextWidth;
	}
	
	public void useTextWidth(boolean useTextWidth) {
		this.useTextWidth = useTextWidth;
	}
	
	public boolean isUsingTextHeight() {
		return useTextHeight;
	}
	
	public void useTextHeight(boolean useTextHeight) {
		this.useTextHeight = useTextHeight;
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
	 * Returns the Paint of the text.
	 * @return The Paint of the text.
	 */
	public Paint getTextPaint() {
		return textPaint;
	}
	
	/**
	 * Sets the Paint of the text.
	 * @param paint The new Paint of the text.
	 */
	public void setTextPaint(Paint paint) {
		textPaint = paint;
	}
	
	/**
	 * Returns the background Paint.
	 * @return The background Paint.
	 */
	public Paint getBackground() {
		return background;
	}
	
	/**
	 * Sets the background Paint.
	 * @param paint The background Paint to set.
	 */
	public void setBackground(Paint paint) {
		background = paint;
	}
	
	/**
	 * Convenience method to set a gradient background. The top color is white.
	 * Automatically sets the background pressed gradient Paint to a color darker than the specified one.
	 * @param bottom The bottom color of the gradient.
	 */
	public void setBackgroundGradient(Color bottom) {
		setBackgroundGradient(Color.white,bottom);
	}
	
	/**
	 * Convenience method to set a gradient background. The top color is white.
	 * Automatically sets the background pressed gradient Paint to a color darker than the specified one.
	 * @param top The top color of the gradient.
	 * @param bottom The bottom color of the gradient.
	 */
	public void setBackgroundGradient(Color top, Color bottom) {
		this.background = new GradientPaint((float)(getX()+getWidth()/2.0),(float)getY(),top,(float)(getX()+getWidth()/2.0),(float)(getY()+getHeight()),bottom);
		setBackgroundPressedGradient(bottom.darker());
	}
	
	/**
	 * Returns the background Paint shown when the button is highlighted.
	 * @return The background Paint shown when the button is highlighted.
	 */
	public Paint getBackgroundHighlight() {
		return backgroundHighlight;
	}
	
	/**
	 * Sets the background Paint shown when the button is highlighted.
	 * @param paint The background Paint to show when the button is highlighted.
	 */
	public void setBackgroundHighlight(Paint paint) {
		backgroundHighlight = paint;
	}
	
	/**
	 * Convenience method to set a gradient background Paint shown when the button is highlighted. The top color is white.
	 * @param bottom The bottom color of the gradient.
	 */
	public void setBackgroundHighlightGradient(Color bottom) {
		setBackgroundHighlightGradient(Color.white,bottom);
	}
	
	/**
	 * Convenience method to set a gradient background Paint shown when the button is highlighted.
	 * @param top The top color of the gradient.
	 * @param bottom The bottom color of the gradient.
	 */
	public void setBackgroundHighlightGradient(Color top, Color bottom) {
		backgroundHighlight = new GradientPaint((float)(getX()+getWidth()/2.0),(float)getY(),top,(float)(getX()+getWidth()/2.0),(float)(getY()+getHeight()),bottom);
	}
	
	/**
	 * Returns the background Paint shown when the button is pressed.
	 * @return The background Paint shown when the button is pressed.
	 */
	public Paint getBackgroundPressed() {
		return backgroundPressed;
	}
	
	/**
	 * Sets the background Paint shown when the button is pressed.
	 * @param paint The background Paint to show when the button is pressed
	 */
	public void setBackgroundPressed(Paint paint) {
		backgroundPressed = paint;
	}
	
	/**
	 * Convenience method to set a gradient background Paint when the button is pressed. The bottom color is white.
	 * @param top The top color of the gradient.
	 */
	public void setBackgroundPressedGradient(Color top) {
		setBackgroundPressedGradient(top,Color.white);
	}
	
	/**
	 * Convenience method to set a gradient background Paint when the button is pressed.
	 * @param top The top color of the gradient.
	 * @param bottom The bottom color of the gradient.
	 */
	public void setBackgroundPressedGradient(Color top, Color bottom) {
		backgroundPressed = new GradientPaint((float)(getX()+getWidth()/2.0),(float)getY(),top,(float)(getX()+getWidth()/2.0),(float)(getY()+getHeight()),bottom);
	}
	
	/**
	 * Returns the border Paint.
	 * @return The border Paint.
	 */
	public Paint getBorder() {
		return border;
	}
	
	/**
	 * Sets the border Paint.
	 * @param paint The border Paint to set.
	 */
	public void setBorder(Paint paint) {
		border = paint;
	}
	
	/**
	 * Returns the border Paint shown when the button is highlighted.
	 * @return The border Paint shown when the button is highlighted.
	 */
	public Paint getBorderHighlight() {
		return borderHighlight;
	}
	
	/**
	 * Sets the border Paint shown when the button is highlighted.
	 * @param paint The border Paint to show when the button is highlighted.
	 */
	public void setBorderHighlight(Paint paint) {
		borderHighlight = paint;
	}
	
	/**
	 * Returns the border Paint shown when the button is pressed.
	 * @return The border Paint shown when the button is pressed.
	 */
	public Paint getBorderPressed() {
		return borderPressed;
	}
	
	/**
	 * Sets the border Paint shown when the button is pressed.
	 * @param paint The border Paint to show when the button is pressed.
	 */
	public void setBorderPressed(Paint paint) {
		this.borderPressed = paint;
	}
	
	/**
	 * Returns the disabled background Paint.
	 * @return The disabled background Paint.
	 */
	public Paint getDisabledBackground() {
		return disabledBackground;
	}
	
	/**
	 * Sets the disabled background Paint. This is actually an overlay over the normal background.
	 * @param paint The disabled background Paint to set.
	 */
	public void setDisabledBackground(Paint paint) {
		disabledBackground = paint;
	}
	
	/**
	 * Returns the disabled border Paint.
	 * @return The disabled border Paint.
	 */
	public Paint getDisabledBorder() {
		return disabledBorder;
	}
	
	/**
	 * Sets the disabled border Paint.
	 * @param paint The disabled border Paint to set.
	 */
	public void setDisabledBorder(Paint paint) {
		disabledBorder = paint;
	}
	
	@Override
	public void hide() {
		super.hide();
		
		setHighlighted(false);
		setPressed(false);
	}
	
	/**
	 * Draws the button.
	 */
	@Override
	public void draw(Graphics2D g) {
		g.setFont(font);
		
		Paint bg, bordr;
		if(isPressed) {
			bg = backgroundPressed;
			bordr = borderPressed;
		}
		else if(isHighlighted) {
			bg = backgroundHighlight;
			bordr = borderHighlight;
		}
		else {
			bg = background;
			bordr = border;
		}
		
		if(isHighlighted) {
			g.setPaint(background);
			g.fill(getButtonBounds());
		}
		
		g.setPaint(bg);
		g.fill(getButtonBounds());
		
		g.setPaint(bordr);
		g.draw(getButtonBounds());
		
		g.setPaint(textPaint);
		g.drawString(text,(int)Math.round(textX),(int)Math.round(textY));
		
		if(!isEnabled) {
			g.setPaint(disabledBackground);
			g.fill(getButtonBounds());
		}
	}
	
	@Override
	public void mousePressed(MouseEvent me) {
		if(me.getButton() != MouseEvent.BUTTON1)
			return;
		
		setPressed(false);
		setHighlighted(false);
		
		if(getButtonBounds().contains(me.getPoint()) && isEnabled())
			setPressed(true);
	}
	
	@Override
	public void mouseReleased(MouseEvent me) {
		if(me.getButton() != MouseEvent.BUTTON1)
			return;
		
		setHighlighted(false);
		
		if(getButtonBounds().contains(me.getPoint()) && isEnabled()) {
			setHighlighted(true);
			
			if(isPressed())
				getAction().doAction(Button.this);
		}
		
		setPressed(false);
	}
	
	@Override
	public void mouseMoved(MouseEvent me) {
		setHighlighted(false);
		
		if(getButtonBounds().contains(me.getPoint()) && isEnabled() && !isPressed())
			setHighlighted(true);
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