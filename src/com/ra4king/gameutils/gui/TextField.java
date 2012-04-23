package com.ra4king.gameutils.gui;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.ra4king.gameutils.Screen;

public class TextField extends Widget {
	private String text;
	private Paint border, background, textPaint;
	private FontMetrics fontMetrics;
	private boolean isPasswordField, showCursor;
	private char passwordChar = '*';
	
	public TextField(double x, double y, double width) {
		this(x,y,width,false);
	}
	
	public TextField(double x, double y, double width, boolean isCentered) {
		this((String)null,x,y,width,isCentered);
	}
	
	public TextField(double x, double y, double width, boolean isCentered, boolean isPasswordField) {
		this((String)null,x,y,width,isCentered,isPasswordField);
	}
	
	public TextField(String text, double x, double y, double width, boolean isCentered) {
		this(text,null,null,x,y,width,isCentered);
	}
	
	public TextField(String text, double x, double y, double width, boolean isCentered, boolean isPasswordField) {
		this(text,null,null,x,y,width,isCentered,isPasswordField);
	}
	
	public TextField(Font font, double x, double y, double width, boolean isCentered) {
		this(null,font,x,y,width,isCentered);
	}
	
	public TextField(Font font, double x, double y, double width, boolean isCentered, boolean isPasswordField) {
		this(null,font,x,y,width,isCentered,isPasswordField);
	}
	
	public TextField(Paint paint, double x, double y, double width, boolean isCentered) {
		this(paint,null,x,y,width,isCentered);
	}
	
	public TextField(Paint paint, double x, double y, double width, boolean isCentered, boolean isPasswordField) {
		this(paint,null,x,y,width,isCentered,isPasswordField);
	}
	
	public TextField(Paint paint, Font font, double x, double y, double width, boolean isCentered) {
		this(null,paint,font,x,y,width,isCentered);
	}
	
	public TextField(Paint paint, Font font, double x, double y, double width, boolean isCentered, boolean isPasswordField) {
		this(null,paint,font,x,y,width,isCentered,isPasswordField);
	}
	
	public TextField(String text, Paint paint, Font font, double x, double y, double width, boolean isCentered) {
		this(text,paint,font,x,y,width,isCentered,false);
	}
	
	public TextField(String text, Paint paint, Font font, double x, double y, double width, boolean isCentered, boolean isPasswordField) {
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
		
		this.isPasswordField = isPasswordField;
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
	
	public void setPasswordField(boolean isPasswordField) {
		this.isPasswordField = isPasswordField;
	}
	
	public boolean isPasswordField() {
		return isPasswordField;
	}
	
	public void setPasswordChar(char pwChar) {
		passwordChar = pwChar;
	}
	
	public char getPasswordChar() {
		return passwordChar;
	}
	
	public Font getFont() {
		return fontMetrics.getFont();
	}
	
	public void init(Screen screen) {
		super.init(screen);
	}
	
	public void draw(Graphics2D g) {
		g.setPaint(hasFocus() ? Color.cyan : border);
		g.draw(getBounds());
		
		g.setPaint(background);
		g.fill(getBounds());
		
		g.setFont(getFont());
		g.setPaint(textPaint);
		
		String s;
		
		if(isPasswordField) {
			String pw = "";
			for(int a = 0; a < text.length(); a++)
				pw += passwordChar;
			s = pw;
		}
		else
			s = text;
		
		g.drawString(s,getIntX()+5,getIntY()+getIntHeight()-10);
		
		if(hasFocus() && (System.currentTimeMillis()/500%2 == 0 || showCursor)) {
			int x = getIntX()+5+fontMetrics.stringWidth(s.substring(0,cursor));
			g.drawLine(x, getIntY()+5, x, getIntY()+getIntHeight()-5);
		}
	}
	
	private int cursor;
	
	public void keyPressed(KeyEvent key) {
		showCursor = true;
		
		if(key.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if(cursor == 0)
				return;
			
			text = text.substring(0,cursor-1) + text.substring(cursor);
			cursor--;
		}
		else if(key.getKeyCode() == KeyEvent.VK_DELETE) {
			if(cursor == text.length())
				return;
			
			text = text.substring(0,cursor) + text.substring(cursor+1);
		}
		else if(key.getKeyCode() == KeyEvent.VK_LEFT) {
			cursor--;
			
			if(cursor < 0)
				cursor = 0;
		}
		else if(key.getKeyCode() == KeyEvent.VK_RIGHT) {
			cursor++;
			
			if(cursor > text.length())
				cursor = text.length();
		}
		else if((key.getModifiersEx()&KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
			
		}
		else if(Character.isLetterOrDigit(key.getKeyCode()) || isSymbol(key.getKeyCode())) {
			String old = text;
			text = text.substring(0,cursor) + key.getKeyChar() + text.substring(cursor);
			
			cursor++;
			
			if(fontMetrics.stringWidth(text) >= getWidth()-5) {
				text = old;
				cursor--;
			}
		}
	}
	
	public void keyReleased(KeyEvent key) {
		showCursor = false;
	}
	
	private boolean isSymbol(int keyCode) {
		switch(keyCode) {
			case KeyEvent.VK_BACK_QUOTE:
			case KeyEvent.VK_SLASH:
			case KeyEvent.VK_PERIOD:
			case KeyEvent.VK_COMMA:
			case KeyEvent.VK_SEMICOLON:
			case KeyEvent.VK_QUOTE:
			case KeyEvent.VK_OPEN_BRACKET:
			case KeyEvent.VK_CLOSE_BRACKET:
			case KeyEvent.VK_BACK_SLASH:
			case KeyEvent.VK_MINUS:
			case KeyEvent.VK_EQUALS:
				return true;
			default:
				return false;
		}
	}
}
