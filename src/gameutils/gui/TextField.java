package gameutils.gui;

import gameutils.Screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class TextField extends Widget {
	private String text;
	private Paint border, background, textPaint;
	private FontMetrics fontMetrics;
	
	public TextField(double x, double y, double width, boolean isCentered) {
		this((String)null,x,y,width,isCentered);
	}
	
	public TextField(String text, double x, double y, double width, boolean isCentered) {
		this(text,null,null,x,y,width,isCentered);
	}
	
	public TextField(Font font, double x, double y, double width, boolean isCentered) {
		this(null,font,x,y,width,isCentered);
	}
	
	public TextField(Paint paint, double x, double y, double width, boolean isCentered) {
		this(paint,null,x,y,width,isCentered);
	}
	
	public TextField(Paint paint, Font font, double x, double y, double width, boolean isCentered) {
		this(null,paint,font,x,y,width,isCentered);
	}
	
	public TextField(String text, Paint paint, Font font, double x, double y, double width, boolean isCentered) {
		if(text == null)
			text = "";
		this.text = text;
		
		if(paint == null)
			paint = Color.black;
		textPaint = paint;
		
		border = Color.black;
		background = new Color(0,0,0,0);
		
		if(font == null)
			font = new Font(Font.SANS_SERIF,Font.TRUETYPE_FONT,20);
		setFont(font);
		
		setWidth(width);
		setHeight(fontMetrics.getHeight()+10);
		
		if(isCentered) {
			setX(x - getWidth()/2);
			setY(y - getHeight()/2);
		}
		else {
			setX(x);
			setY(y);
		}
	}
	
	public void setBorder(Paint paint) {
		this.border = paint;
	}
	
	public Paint getBorder() {
		return border;
	}
	
	public void setBackground(Paint paint) {
		this.background = paint;
	}
	
	public Paint getBackground() {
		return background;
	}
	
	public void setTextPaint(Paint paint) {
		this.textPaint = paint;
	}
	
	public Paint getTextPaint() {
		return textPaint;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void setFont(Font font) {
		Graphics2D g = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB).createGraphics();
		fontMetrics = g.getFontMetrics(font);
		g.dispose();
	}
	
	public Font getFont() {
		return fontMetrics.getFont();
	}
	
	public void init(Screen screen) {
		super.init(screen);
	}
	
	public void update(long deltaTime) {
	}
	
	public void draw(Graphics2D g) {
		g.setPaint(border);
		g.draw(getBounds());
		
		g.setPaint(background);
		g.fill(getBounds());
		
		g.setFont(getFont());
		g.setPaint(textPaint);
		g.drawString(text,getIntX()+5,getIntY()+getIntHeight()-10);
	}
	
	public void keyPressed(KeyEvent key) {
		if(Character.isLetterOrDigit(key.getKeyChar()) || key.getKeyCode() == KeyEvent.VK_SPACE) {
			text += key.getKeyChar();
			
			while(fontMetrics.stringWidth(text) >= getWidth()-6)
				text = text.substring(0,text.length()-1);
		}
		else if(text.length() > 0 && key.getKeyCode() == KeyEvent.VK_BACK_SPACE)
			text = text.substring(0,text.length()-1);
	}
	
	private Paint oldBorder;
	
	public void focusGained() {
		oldBorder = border;
		border = Color.cyan;
	}
	
	public void focusLost() {
		border = oldBorder;
	}
}
