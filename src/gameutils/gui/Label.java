package gameutils.gui;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * A MenuLabel extends MenuItem and just draws a String.
 * @author Roi Atalla
 */
public class Label extends Widget {
	private String text;
	private Color color;
	private Font font;
	private int centerX, centerY;
	private boolean isCentered;
	
	/**
	 * Initializes this object.
	 * @param text The text to show.
	 * @param color The color of the text.
	 * @param font The font of the text.
	 * @param x The X position of the text.
	 * @param y The Y position of the text.
	 * @param centered If true, the X and Y are the center of the text, else they are the top left corner of the text.
	 */
	public Label(String text, Color color, Font font, int x, int y, boolean centered) {
		if(text == null)
			this.text = "";
		else
			this.text = text;
		
		if(color == null)
			this.color = Color.black;
		else
			this.color = color;
		
		if(font == null)
			this.font = new Font(Font.SANS_SERIF,Font.BOLD,20);
		else
			this.font = font;
		
		centerX = x;
		centerY = y;
		
		isCentered = centered;
		
		recalcCoords();
	}
	
	/**
	 * Initializes this object.
	 * @param text The text to show.
	 * @param color The color of the text.
	 * @param fontSize The font size of the text. The default font is a Bold Sans-Serif.
	 * @param x The X position of the text.
	 * @param y The Y position of the text.
	 * @param centered If true, the X and Y are the center of the text, else they are the top left corner of the text.
	 */
	public Label(String text, Color color, int fontSize, int x, int y, boolean centered) {
		this(text,color,new Font(Font.SANS_SERIF,Font.BOLD,fontSize),x,y,centered);
	}
	
	/**
	 * Initializes this object.
	 * @param text The text to show. The default color is black.
	 * @param fontSize The font size of the text. The default font is a Bold Sans-Serif.
	 * @param x The X position of the text.
	 * @param y The Y position of the text.
	 * @param centered If true, the X and Y are the center of the text, else they are the top left corner of the text.
	 */
	public Label(String text, int fontSize, int x, int y, boolean centered) {
		this(text,Color.black,new Font(Font.SANS_SERIF,Font.BOLD,fontSize),x,y,centered);
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
		if(isCentered) {
			Graphics2D g = (Graphics2D)new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB).getGraphics();
			FontMetrics fm = g.getFontMetrics(font);
			int width = fm.stringWidth(text);
			super.setX((int)(centerX-width/2));
			super.setY((int)(centerY+fm.getHeight()/2));
		}
		else {
			super.setX(centerX);
			super.setY(centerY);
		}
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
	public Color getColor() {
		return color;
	}
	
	/**
	 * Sets the color of the text.
	 * @param color The new color of the text.
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void update(long deltaTime) {}
	
	/**
	 * Draws the text with the specified font and color.
	 * @param g The Graphics context to draw to the screen.
	 */
	public void draw(Graphics2D g) {
		g.setColor(color);
		g.setFont(font);
		
		g.drawString(text,getIntX(),getIntY());
	}
}