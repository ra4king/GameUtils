package com.ra4king.gameutils.util;


import java.awt.Image;
import java.util.ArrayList;

import com.ra4king.gameutils.Art;

/**
 * A convenience Animation class used to animate 2D sprites.
 * @author Roi Atalla
 */
public class Animation {
	private ArrayList<Frame> frames;
	private long totalTime, currentTime;
	private int frameIndex;
	
	/**
	 * Default constructor.
	 */
	public Animation() {
		frames = new ArrayList<Frame>();
	}
	
	/**
	 * Adds an image to the animation.
	 * @param i The image to be added.
	 * @param time The amount of time in milliseconds this image is displayed.
	 */
	public void addFrame(Image i, long time) {
		totalTime += time;
		frames.add(new Frame(i,totalTime));
	}
	
	/**
	 * Splits the image into separate frames depending on the width and height.
	 * @param i The image to be split.
	 * @param width The width of each frame.
	 * @param height The height of each frame.
	 * @param time The duration of each frame.
	 * @throws IllegalArgumentException If the image cannot be split evenly or the length of the array does not equal rows times columns.
	 */
	public void addFrames(Image i, int width, int height, long time) {
		int rows = i.getWidth(null)/width;
		int cols = i.getHeight(null)/height;
		
		long times[] = new long[rows*cols];
		for(int a = 0; a < times.length; a++)
			times[a] = time;
		
		addFrames(i,width,height,times);
	}
	
	/**
	 * Splits the image into separate frames depending on the width and height.
	 * @param i The image to be split.
	 * @param width The width of each frame.
	 * @param height The height of each frame.
	 * @param times The duration of each frame.
	 * @throws IllegalArgumentException If the image cannot be split evenly or the length of the array does not equal rows times columns.
	 */
	public void addFrames(Image i, int width, int height, long times[]) {
		int rows = i.getWidth(null)/width;
		int cols = i.getHeight(null)/height;
		
		if(rows*cols != times.length)
			throw new IllegalArgumentException("Not enough frame durations supplied.");
		
		Image images[][] = Art.split(i, width, height);
		
		for(Image row[] : images)
			for(Image col : row)
				addFrame(col,times[rows*cols]);
	}
	
	/**
	 * Returns the current image displayed.
	 * @return The current image displayed.
	 */
	public Image getFrame() {
		return frames.size() == 0 ? null : frames.get(frameIndex).i;
	}
	
	/**
	 * Must be called to update the animation
	 * @param deltaTime The time passed since the last call to it.
	 */
	public void update(long deltaTime) {
		if(frames.size() > 1) {
			currentTime += deltaTime;
			
			if(currentTime > totalTime) {
				frameIndex = 0;
				currentTime %= totalTime;
			}
			
			while(currentTime > frames.get(frameIndex).time)
				frameIndex++;
		}
	}
	
	private static class Frame {
		private Image i;
		private long time;
		
		public Frame(Image i, long time) {
			this.i = i;
			this.time = time;
		}
	}
}
