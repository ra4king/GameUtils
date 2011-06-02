package gameutils;

import java.awt.Image;
import java.util.ArrayList;

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
			
			if(currentTime > totalTime)
				currentTime = frameIndex = 0;
			
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
