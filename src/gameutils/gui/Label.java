package gameutils.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;

/**
 * A Label extends Widget and just draws a String. If you are confused with the usage of Paint, remember Color implements Paint so you can still a Color if you wish.
 * @author Roi Atalla
 */
public class Label extends Widget {
	private String text;
	private Paint background, textPaint;
	private Font font;
	private int textX, textY;
	private boolean isCentered;
	
	/**
	 * Initializes this object. Background is set to transparent.
	 * @param text The text to show. If this is null, an empty string is set.
	 * @param paint The Paint of the text. If this is null, the Paint is set to Color.black.
	 * @param font The font of the text. If this is null, it uses a Bold Sans Serif with a point size of 20.
	 * @param x The X position of the text.
	 * @param y The Y position of the text.
	 * @param centered If true, the X and Y are the center of the text, else they are the top left corner of the text.
	 */
	public Label(String text, Paint paint, Font font, int x, int y, boolean centered) {
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
		
		textX = x;
		textY = y;
		
		isCentered = centered;
		
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
		
		if(isCentered) {
			textX -= width/2;
			textY += fm.getHeight()/2;
			super.setX(textX);
			super.setY(textY-fm.getAscent());
		}
		else {
			super.setX(textX);
			super.setY(textY-fm.getAscent());
		}
		
		super.setWidth(width);
		super.setHeight(height);
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
		
		g.drawString(text,textX,textY);
	}
}