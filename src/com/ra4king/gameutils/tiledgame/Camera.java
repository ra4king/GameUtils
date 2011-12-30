package com.ra4king.gameutils.tiledgame;

public class Camera {
	private int screenWidth, screenHeight;
	public int xOffset, yOffset;
	
	public Camera(int width, int height) {
		screenWidth = width;
		screenHeight = height;
	}
	
	public void centerAt(int x, int y) {
		xOffset = -x+screenWidth/2;
		yOffset = -y+screenHeight/2;
	}
}
