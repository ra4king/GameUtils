package com.ra4king.gameutils;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * A class that stores sound clips.
 * @author Roi Atalla
 */
public class Sound extends Assets<Clip> {
	private volatile boolean on = true;
	
	Sound() {}
	
	@Override
	public Clip extract(URL url) throws IOException {
		try{
			Clip clip;
			
			AudioInputStream in = AudioSystem.getAudioInputStream(url);
			
			if(url.getPath().toLowerCase().endsWith(".ogg")) {
				AudioFormat baseFormat = in.getFormat();
				AudioFormat  decodedFormat = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						baseFormat.getSampleRate(),
						16,
						baseFormat.getChannels(),
						baseFormat.getChannels() * 2,
						baseFormat.getSampleRate(),
						false);
				in = AudioSystem.getAudioInputStream(decodedFormat, in);
			}
			
			clip = AudioSystem.getClip();
			
			clip.open(in);
			
			return clip;
		}
		catch(IOException exc) {
			throw exc;
		}
		catch(Exception exc) {
			throw new IOException("Error loading clip: " + url,exc);
		}
	}
	
	/**
	 * Plays the Clip. If the sound is off, the volume is set to the minimum.
	 * @param name The name of the Clip to play.
	 */
	public void play(String name) {
		Clip c = get(name);
		
		if(c != null) {
			c.stop();
			c.flush();
			c.setFramePosition(0);
			
			if(!on) {
				FloatControl volume = (FloatControl)c.getControl(FloatControl.Type.MASTER_GAIN);
				volume.setValue(volume.getMinimum());
			}
			
			c.start();
		}
	}
	
	/**
	 * Loops the Clip. If the sound is off, the volume is set to the minimum.
	 * @param name The name of the Clip to loop.
	 */
	public void loop(String name) {
		Clip c = get(name);
		
		if(c != null) {
			c.stop();
			c.flush();
			c.setFramePosition(0);
			
			if(!on) {
				FloatControl volume = (FloatControl)c.getControl(FloatControl.Type.MASTER_GAIN);
				volume.setValue(volume.getMinimum());
			}
			
			c.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	/**
	 * Pauses all currently playing Clips.
	 */
	public void pause() {
		for(Clip c : assets.values())
			c.stop();
	}
	
	/**
	 * Plays all currently paused Clips.
	 */
	public void resume() {
		for(Clip c : assets.values()) {
			if(on && c.getMicrosecondPosition() != c.getMicrosecondLength() &&
					c.getMicrosecondPosition() != 0)
				c.start();
		}
	}
	
	/**
	 * Pauses the specified Clip.
	 * @param name The name of the Clip to pause.
	 */
	public void pause(String name) {
		Clip c = get(name);
		
		if(c != null)
			c.stop();
	}
	
	/**
	 * Stops and resets the specified Clip.
	 * @param name The name of the Clip to stop.
	 */
	public void stop(String name) {
		Clip c = get(name);
		
		if(c != null) {
			c.stop();
			c.flush();
			c.setFramePosition(0);
		}
	}
	
	public void stopAll() {
		for(String s : assets.keySet())
			stop(s);
	}
	
	/**
	 * Sets the sound on/off.
	 * @param isOn If true, all currently playing Clips are set to the minimum volume, else they are set to default volume.
	 */
	public void setOn(boolean isOn) {
		on = isOn;
		
		for(Clip c : assets.values()) {
			FloatControl volume = (FloatControl)c.getControl(FloatControl.Type.MASTER_GAIN);
			if(on)
				volume.setValue(0);
			else
				volume.setValue(volume.getMinimum());
		}
	}
	
	/**
	 * Returns whether the sound is on/off.
	 * @return True if the sound is on, else the sound is off.
	 */
	public boolean isOn() {
		return on;
	}
}