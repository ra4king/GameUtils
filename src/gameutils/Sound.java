package gameutils;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * A class that stores sound clips.
 * @author Roi Atalla
 */
public class Sound extends Assets<Clip> {
	private volatile boolean on = true;
	
	public Clip extract(URL url) throws IOException {
		try{
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(url));
			return clip;
		}
		catch(Exception exc) {
			throw new IOException("Error loading clip: " + url + ": " + exc);
		}
	}
	
	/**
	 * Plays the Clip. If the sound is off, the volume is set to the minimum.
	 * @param name The name of the Clip to play.
	 */
	public void play(String name) {
		get(name).stop();
		get(name).setMicrosecondPosition(0);
		
		if(!on) {
			FloatControl volume = (FloatControl)get(name).getControl(FloatControl.Type.MASTER_GAIN);
			volume.setValue(volume.getMinimum());
		}
		
		get(name).start();
	}
	
	/**
	 * Loops the Clip. If the sound is off, the volume is set to the minimum.
	 * @param name The name of the Clip to loop.
	 */
	public void loop(String name) {
		get(name).stop();
		get(name).setMicrosecondPosition(0);
		
		if(!on) {
			FloatControl volume = (FloatControl)get(name).getControl(FloatControl.Type.MASTER_GAIN);
			volume.setValue(volume.getMinimum());
		}
		
		get(name).loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	/**
	 * Pauses all currently playing Clips.
	 */
	public synchronized void pause() {
		for(Clip c : assets.values())
			c.stop();
	}
	
	/**
	 * Plays all currently paused Clips.
	 */
	public synchronized void resume() {
		for(Clip c : assets.values()) {
			if(on && c.getMicrosecondPosition() != c.getMicrosecondLength() &&
					c.getMicrosecondPosition() != 0) {
				c.start();
			}
		}
	}
	
	/**
	 * Pauses the specified Clip.
	 * @param name The name of the Clip to pause.
	 */
	public void pause(String name) {
		get(name).stop();
	}
	
	/**
	 * Stops and resets the specified Clip.
	 * @param name The name of the Clip to stop.
	 */
	public void stop(String name) {
		get(name).stop();
		get(name).setMicrosecondPosition(0);
	}
	
	/**
	 * Sets the sound on/off.
	 * @param isOn If true, all currently playing Clips are set to the minimum volume, else they are set to default volume.
	 */
	public synchronized void setOn(boolean isOn) {
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