package gameutils;

import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * A class that stores sound clips. It uses the main class's folder as the source.
 * @author Roi Atalla
 */
public class Sound {
	private Map<String,Clip> clips;
	private Class<?> clazz;
	private volatile boolean on = true;
	
	Sound() {
		clips = new Hashtable<String,Clip>();
	}
	
	/**
	 * Returns the canonical name of this file path. It strips the path and the extension.
	 * @param file The file to be canonicalized.
	 * @return The canonical name of this file.
	 */
	public static String getFileName(String file) {
		file = file.replace("\\","/");
		return file.substring(file.lastIndexOf("/")+1,file.lastIndexOf("."));
	}
	
	/**
	 * Sets the source class. This is automatically set as the main class's folder.
	 * @param clazz
	 */
	public void setSourceClass(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	/**
	 * Gets the sound file by using <code>Class.getResource(String)</code> and opens a Clip on it.
	 * Its associated name is set as its canonical name.
	 * @param file The file to import.
	 * @return The Clip opened.
	 */
	public Clip add(String file) {
		return add(file,getFileName(file));
	}
	
	/**
	 * Gets the sound file by using <code>Class.getResource(String)</code> and opens a Clip on it.
	 * @param file The file to import.
	 * @param name The name to associate with this Clip.
	 * @return The Clip opened.
	 */
	public Clip add(String file, String name) {
		return add(clazz.getResource(file),name);
	}
	
	/**
	 * Opens a Clip on this URL.
	 * @param url The URL of the sound file.
	 * @param name The name to associate with this Clip.
	 * @return The Clip opened.
	 */
	public Clip add(URL url, String name) {
		try{
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(url));
			return add(clip,name);
		}
		catch(Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Adds the Clip.
	 * @param clip The Clip to add.
	 * @param name The name to associate with this clip.
	 * @return The clip that was added.
	 */
	public Clip add(Clip clip, String name) {
		if(clip.isOpen())
			clips.put(name,clip);
		
		return clip;
	}
	
	/**
	 * Returns the Clip with the specified name.
	 * @param name The name of the Clip.
	 * @return The Clip with the specified name.
	 */
	public Clip getClip(String name) {
		return clips.get(name.intern());
	}
	
	/**
	 * Renames the Clip with the specified name.
	 * @param oldName The old name.
	 * @param newName The new name.
	 * @throws Exception
	 */
	public void rename(String oldName, String newName) {
		add(remove(oldName.intern()),newName.intern());
	}
	
	/**
	 * Replaces the Clip associated with the specified name with the specified Clip. Does the same thing as <code>add(Clip,String)</code>
	 * @param oldName The name of the old Clip.
	 * @param newClip The new Clip that will replace the old Clip.
	 * @return The Clip that was replaced.
	 */
	public Clip replace(String oldName, Clip newClip) {
		oldName = oldName.intern();
		
		if(getClip(oldName) == null)
			throw new IllegalArgumentException("Invalid name");
		
		Clip clip = getClip(oldName);
		add(newClip,oldName);
		return clip;
	}
	
	/**
	 * Swaps the Clips of both specified names.
	 * @param first The first Clip.
	 * @param second The second Clip.
	 */
	public synchronized void swap(String first, String second) {
		Clip c = clips.get(first.intern());
		Clip c2 = clips.get(second.intern());
		
		if(c == null)
			throw new IllegalArgumentException("First name is invalid.");
		if(c2 == null)
			throw new IllegalArgumentException("Second name is invalid");
		
		clips.put(second.intern(),clips.put(first.intern(),clips.get(second.intern())));
	}
	
	/**
	 * Removes the Clip.
	 * @param name The name of the Clip.
	 * @return The Clip that was removed.
	 */
	public synchronized Clip remove(String name) {
		Clip c = getClip(name);
		clips.remove(name.intern());
		return c;
	}
	
	/**
	 * Plays the Clip. If the sound is off, the volume is set to the minimum.
	 * @param name The name of the Clip to play.
	 */
	public void play(String name) {
		getClip(name).stop();
		getClip(name).setMicrosecondPosition(0);
		
		if(!on) {
			FloatControl volume = (FloatControl)getClip(name).getControl(FloatControl.Type.MASTER_GAIN);
			volume.setValue(volume.getMinimum());
		}
		
		getClip(name).start();
	}
	
	/**
	 * Loops the Clip. If the sound is off, the volume is set to the minimum.
	 * @param name The name of the Clip to loop.
	 */
	public void loop(String name) {
		getClip(name).stop();
		getClip(name).setMicrosecondPosition(0);
		
		if(!on) {
			FloatControl volume = (FloatControl)getClip(name).getControl(FloatControl.Type.MASTER_GAIN);
			volume.setValue(volume.getMinimum());
		}
		
		getClip(name).loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	/**
	 * Pauses all currently playing Clips.
	 */
	public void pause() {
		for(Clip c : clips.values())
			c.stop();
	}
	
	/**
	 * Plays all currently paused Clips.
	 */
	public void resume() {
		for(Clip c : clips.values()) {
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
		getClip(name).stop();
	}
	
	/**
	 * Stops and resets the specified Clip.
	 * @param name The name of the Clip to stop.
	 */
	public void stop(String name) {
		getClip(name).stop();
		getClip(name).setMicrosecondPosition(0);
	}
	
	/**
	 * Sets the sound on/off.
	 * @param isOn If true, all currently playing Clips are set to the minimum volume, else they are set to default volume.
	 */
	public void setOn(boolean isOn) {
		on = isOn;
		
		for(Clip c : clips.values()) {
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
	
	/**
	 * A convenience class that buffers Clips and loads them all at once.
	 * @author Roi Atalla
	 */
	public class Loader implements Runnable {
		private Map<String,String> files = new Hashtable<String,String>();
		private int status;
		
		/**
		 * Returns the total number of Clips in this buffer.
		 * @return The total number of Clips in this buffer.
		 */
		public int getTotal() {
			return files.size();
		}
		
		/**
		 * Returns the number of Clips added and loaded.
		 * @return The number of Clips added and loaded.
		 */
		public int getStatus() {
			return status;
		}
		
		/**
		 * Adds the file to the buffer.
		 * @param file The file to be loaded. The associated name is its canonical name.
		 */
		public void addFile(String file) {
			addFile(file,Sound.getFileName(file));
		}

		/**
		 * Adds the file and sets the name associated with it.
		 * @param file The file to be loaded.
		 * @param name The name to be associated to this Clip.
		 */
		public void addFile(String file, String name) {
			files.put(name,file);
		}
		
		/**
		 * A list of files to be added.
		 * @param files The list of files to be added. Their associated names will be their canonical names.
		 */
		public void addFiles(String ... files) {
			for(String s : files)
				addFile(s);
		}
		
		/**
		 * A list of files to be added.
		 * @param files The list of files to be added. Index 0 is the file path. Index 1 is the associated name.
		 */
		public void addFiles(String[] ... files) {
			for(String[] s: files)
				addFile(s[0],s[1]);
		}
		
		/**
		 * Spawns a new thread and adds all Clips to the Sound instance.
		 */
		public synchronized void start() {
			new Thread(this).start();
		}
		
		/**
		 * Adds all Clips to the Sound instance.
		 */
		public synchronized void run() {
			for(String s : files.keySet()) {
				try{
					add(files.get(s),s);
					status++;
				}
				catch(Exception exc) {
					exc.printStackTrace();
					status = -1;
					return;
				}
			}
		}
	}
}