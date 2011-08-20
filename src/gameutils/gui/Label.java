package gameutils.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;

/**
 * A Label extends Widget and just draws a String. If you are confused with the usage of Paint, remember Color implements Paint so you can still use a Color if you wish.
 * @author Roi Atalla
 */
public class Label extends Widget {
	private String text;
	private Paint background, textPaint;
	private Font font;
	private double textX, textY, centerX, centerY;
	private boolean isCentered;
	private boolean randomColors;
	
	public Label(String text, double x, double y) {
		this(text,x,y,false);
	}
	
	public Label(String text, double x, double y, boolean centered) {
		this(text,null,null,x,y,centered);
	}
	
	public Label(String text, Paint paint, double x, double y, boolean centered) {
		this(text,paint,null,x,y,centered);
	}
	
	public Label(String text, Font font, double x, double y, boolean centered) {
		this(text,null,font,x,y,centered);
	}
	
	/**
	 * Initializes this object. Background is set to transparent.
	 * @param text The text to show. If this is null, an empty string is set.
	 * @param paint The Paint of the text. If this is null, the Paint is set to Color.black.
	 * @param font The font of the text. If this is null, it uses a Bold Sans Serif with a point size of 20.
	 * @param x The X position of the text.
	 * @param y The Y position of the text.
	 * @param centered If true, the X and Y are the center of the text, else they are the top left corner of the text.
	 */
	public Label(String text, Paint paint, Font font, double x, double y, boolean centered) {
		if(text == null)
			this.text = "";
		else
			this.text = text;
		
		if(paint == null)
			this.textPaint = Color.black;
		else
			this.textPaint = paint;
		
		background = new Color(0,0,0,0);
		
		if(font == null)
			this.font = new Font(Font.SANS_SERIF,Font.BOLD,20);
		else
			this.font = font;
		
		isCentered = centered;
		
		if(isCentered) {
			centerX = x;
			centerY = y;
		}
		else {
			textX = x;
			textY = y;
		}
		
		recalcCoords();
	}
	
	/**
	 * Initializes this object.
	 * @param text The text to show. If this is null, an empty string is set.
	 * @param paint The Paint of the text. If this is null, the Paint is set to Color.black.
	 * @param fontSize The font size of the text. The default font is a Bold Sans Serif.
	 * @param x The X position of the text.
	 * @param y The Y position of the text.
	 * @param centered If true, the X and Y are the center of the text, else they are the top left corner of the text.
	 */
	public Label(String text, Paint paint, int fontSize, int x, int y, boolean centered) {
		this(text,paint,new Font(Font.SANS_SERIF,Font.BOLD,fontSize),x,y,centered);
	}
	
	/**
	 * Initializes this object.
	 * @param text The text to show. If this is null, an empty string is set. The default color is black.
	 * @param fontSize The font size of the text. The default font is a Bold Sans-Serif.
	 * @param x The X position of the text.
	 * @param y The Y position of the text.
	 * @param centered If true, the X and Y are the center of the text, else they are the top left corner of the text.
	 */
	public Label(String text, int fontSize, int x, int y, boolean centered) {
		this(text,null,new Font(Font.SANS_SERIF,Font.BOLD,fontSize),x,y,centered);
	}
	
	/**
	 * Returns the text that is shown.
	 * @return The text that is shown. 
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Sets the text to show. If the current text is the same as the description, the description is set the specified text, else the desription is untouched.
	 * @param text The new text to show.
	 */
	public void setText(String text) {
		this.text = text;
		
		recalcCoords();
	}
	
	/**
	 * Returns the font used to display the text.
	 * @return The font used to display the text.
	 */
	public Font getFont() {
		return font;
	}
	
	/**
	 * Sets the font of the text.
	 * @param font The new font of the text.
	 */
	public void setFont(Font font) {
		this.font = font;
		
		recalcCoords();
	}
	
	private void recalcCoords() {
		Graphics2D g = (Graphics2D)new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB).getGraphics();
		FontMetrics fm = g.getFontMetrics(font);
		int width = fm.stringWidth(text);
		int height = fm.getHeight();
		double x, y;
		
		if(isCentered) {
			textX = x = centerX - width/2;
			textY = y = centerY + fm.getHeight()/2;
		}
		else {
			x = textX;
			y = textY;
		}
		
		super.setX(x);
		super.setY(y-fm.getAscent());
		super.setWidth(width);
		super.setHeight(height);
	}
	
	/**
	 * Calls MenuItem's setX method and recalculates all the coordinates.
	 */
	public void setX(double x) {
		if(isCentered)
			centerX = x;
		else
			textX = x;
		recalcCoords();
	}
	
	/**
	 * Calls MenuItem's setY method and recalculates all the coordinates.
	 */
	public void setY(double y) {
		if(isCentered)
			centerY = y;
		else
			textY = y;
		recalcCoords();
	}
	
	/**
	 * Sets whether the X and Y are the center of the text or not.
	 * @param isCentered If true, the X and Y are the center of the text, else they are absolute positions.
	 */
	public void setCentered(boolean isCentered) {
		this.isCentered = isCentered;
		recalcCoords();
	}
	
	/**
	 * Returns whether the X and Y are the center of the text or not.
	 * @return True if the X and Y are the center of the text, false otherwise.
	 */
	public boolean isCentered() {
		return isCentered;
	}
	
	/**
	 * Returns the color of the text.
	 * @return The color of the text.
	 */
	public Paint getTextPaint() {
		return textPaint;
	}
	
	/**
	 * Sets the Paint of the text.
	 * @param paint The Paint of the text.
	 */
	public void setTextPaint(Paint paint) {
		textPaint = paint;
	}
	
	/**
	 * Returns the background.
	 * @return The background.
	 */
	public Paint getBackground() {
		return background;
	}
	
	/**
	 * Sets the background.
	 * @param paint The background.
	 */
	public void setBackground(Paint paint) {
		background = paint;
	}
	
	public boolean isUsingRandomColors() {
		return randomColors;
	}
	
	public void useRandomColors(boolean useRandom) {
		randomColors = useRandom;
	}
	
	public void paused() {}
	
	public void resumed() {}
	
	public void update(long deltaTime) {}
	
	/**
	 * Fills the background then draws the text with the specified font and paint.
	 * @param g The Graphics context to draw to the screen.
	 */
	public void draw(Graphics2D g) {
		g.setPaint(background);
		g.fill(getBounds());
		
		g.setPaint(textPaint);
		g.setFont(font);
		
		if(randomColors) {
			char chars[] = new char[text.length()];
			text.getChars(0, text.length(), chars, 0);
			int offset = 0;
			for(char c : chars) {
				g.setColor(new Color((int)Math.round(Math.random()*255),(int)Math.round(Math.random()*255),(int)Math.round(Math.random()*255)));
				g.drawString(""+c, (int)Math.round(textX+offset), (int)Math.round(textY));
				offset += g.getFontMetrics().charWidth(c);
			}
		}
		else {
			g.drawString(text,(int)Math.round(textX),(int)Math.round(textY));
		}
	}
}