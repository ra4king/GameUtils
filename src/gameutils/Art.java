package gameutils;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * A class that stores Images.
 * @author Roi Atalla
 */
public class Art {
	private Map<String,Image> images;
	
	Art() {
		images = new HashMap<String,Image>();
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
	 * This method searches the root directory for the file path. Then it obtains the image by using <code>javax.imageio.ImageIO</code>.
	 * Its associated name is its canonical name.
	 * @param filename The file name of the image. Automatically creates a compatible image.
	 * @return The Image itself.
	 * @throws Exception
	 */
	public Image add(String file) {
		return add(file,getFileName(file));
	}
	
	/**
	 * This method searches the root directory for the file path. Then it obtains the image by using <code>javax.imageio.ImageIO</code>.
	 * @param filename The file name of the image. Automatically creates a compatible image.
	 * @param name The name to associate with this image.
	 * @return The Image imported.
	 * @throws Exception
	 */
	public Image add(String file, String name) {
		return add(getClass().getResource("/"+file),name);
	}
	
	/**
	 * Obtains the image using <code>javax.imageio.ImageIO</code> and adds it the map.
	 * @param url The URL to the image. Automatically creates a compatible image.
	 * @param name The name of the image.
	 * @return The Image itself.
	 * @throws Exception
	 */
	public Image add(URL url, String name) {
		try{
			return add(ImageIO.read(url),name);
		}
		catch(Exception exc) {
			throw new IllegalArgumentException("Error loading image: " + url + " with name: " + name);
		}
	}
	
	/**
	 * Adds the image to the map.
	 * @param image The image to be added. Automatically creates a compatible version of it.
	 * @param name The name of the image.
	 * @return The Image itself.
	 */
	public synchronized Image add(Image image, String name) {
		return images.put(name,createCompatibleImage(image));
	}
	
	/**
	 * Returns an image with the specified name.
	 * @param name The name of the image.
	 * @return The image associated with this name, if not found, returns null.
	 */
	public Image get(String name) {
		return images.get(name);
	}
	
	/**
	 * Returns the name associated with this image.
	 * @param image The image who's associated name is returned.
	 * @return The name of the associated image. If it is not found, <code>null</code> is returned.
	 */
	public synchronized String getName(Image image) {
		for(String s : images.keySet()) {
			if(images.get(s) == image || images.get(s).equals(image))
				return s;
		}
		
		return null;
	}
	
	/**
	 * Renames the image with a new name.
	 * @param oldName The image name to be renamed.
	 * @param newName The new named to be used.
	 */
	public void rename(String oldName, String newName) {
		add(remove(oldName),newName);
	}
	
	/**
	 * Does the same thing as add(Image,String) except it ensures that the image is replaced since add(Image,String) is unreliable due to String's internalization mechanism.
	 * @param oldName The name of the previous image.
	 * @param newImage The new image.
	 * @return The old image.
	 */
	public Image replace(String oldName, Image newImage) {
		if(get(oldName) == null)
			throw new IllegalArgumentException("Invalid name");
		
		return add(newImage,oldName);
	}
	
	/**
	 * Swaps the images associated with both strings.
	 * @param first The name of the first image.
	 * @param second The name of the second image.
	 */
	public void swap(String first, String second) {
		Image i = images.get(first);
		Image i2 = images.get(second);
		
		if(i == null)
			throw new IllegalArgumentException("First name is invalid.");
		if(i2 == null)
			throw new IllegalArgumentException("Second name is invalid");
		
		images.put(second,images.put(first,images.get(second)));
	}
	
	/**
	 * Removes the specified image name.
	 * @param name The name of the image to be removed.
	 * @return The image associated with this name, if not found, returns null.
	 */
	public synchronized Image remove(String name) {
		Image i = images.get(name);
		images.remove(name);
		return i;
	}
	
	/**
	 * Splits the image with the specified width and height of each quadrant and adds the pieces to the map.
	 * @param image The image to be split.
	 * @param width The width of each quadrant inside the image.
	 * @param height the height of each quadrant inside the image.
	 * @return The images split from the original image.
	 */
	public Image[][] splitAndAdd(Image image, int width, int height) {
		Image im[][] = split(image,width,height);
		for(Image i[] : im)
			for(Image i2 : i)
				add(i2,"Image"+images.size());
		return im;
	}
	
	/**
	 * Splits the image with the specified width and height of each quadrant.
	 * @param image The image to be split.
	 * @param width The width of each quadrant inside the image.
	 * @param height The height of each quadrant inside the image.
	 * @return The images split from the original image.
	 */
	public static BufferedImage[][] split(Image image, int width, int height) {
		double r = image.getWidth(null)/(double)width;
		double c = image.getHeight(null)/(double)height;
		
		if(r-(int)r != 0.0 || c-(int)c != 0.0)
			throw new IllegalArgumentException("Image is not evenly divisible.");
		
		BufferedImage newImages[][] = new BufferedImage[image.getHeight(null)/height][image.getWidth(null)/width];
		for(int a = 0; a < newImages.length; a++) {
			for(int b = 0; b < newImages[a].length; b++) {
				newImages[a][b] = createCompatibleImage(width,height);
				Graphics2D g = newImages[a][b].createGraphics();
				g.drawImage(image, 0, 0, width, height, b*width, a*height, b*width+width, a*height+height, null);
				g.dispose();
			}
		}
		return newImages;
	}
	
	/**
	 * Returns a BufferedImage that is compatible with the current display settings. The transparency used is TRANSLUCENT.
	 * @param width The width of the image.
	 * @param height The height of the image.
	 * @return The compatible BufferedImage.
	 */
	public static BufferedImage createCompatibleImage(int width, int height) {
		return createCompatibleImage(width,height,Transparency.TRANSLUCENT);
	}
	
	/**
	 * Returns a BufferedImage that is compatible with the current display settings.
	 * @param width The width of the image.
	 * @param height The height of the image.
	 * @param translucency the translucency of the image. It can be any integer from the java.awt.Transparency class.
	 * @return The compatible BufferedImage.
	 */
	public static BufferedImage createCompatibleImage(int width, int height, int translucency) {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width,height,translucency);
	}
	
	/**
	 * Converts the specified image to one that is compatible with the current display settings.
	 * If it is an instance of BufferedImage, the BufferedImage's transparency is used,
	 * else the returned image will have a transparency of type TRANSLUCENT.
	 * @param image The image to be converted.
	 * @return The converted compatible BufferedImage.
	 */
	public static BufferedImage createCompatibleImage(Image image) {
		return createCompatibleImage(image,(image instanceof BufferedImage) ? ((BufferedImage)image).getTransparency() : Transparency.TRANSLUCENT);
	}
	
	/**
	 * Converts the specified image to one that is compatible with the current display settings.
	 * @param image The image to be converted.
	 * @param transparency The transparency of the new image.
	 * @return The converted compatible BufferedImage.
	 */
	public static BufferedImage createCompatibleImage(Image image, int transparency) {
		BufferedImage newImage = createCompatibleImage(image.getWidth(null),image.getHeight(null),transparency);
		Graphics2D g = newImage.createGraphics();
		g.drawImage(image,0,0,null);
		g.dispose();
		return newImage;
	}
	
	/**
	 * Mirrors the image along the specified axis.
	 * @param image The image to be mirrored.
	 * @param horizontally The axis to mirror over. If true, it will mirror over the X-Axis, else it will mirror over the Y-Axis.
	 * @return The mirrored image.
	 */
	public static Image mirror(Image image, boolean horizontally) {
		BufferedImage newImage = createCompatibleImage(image.getWidth(null),image.getHeight(null),(image instanceof BufferedImage) ? ((BufferedImage)image).getTransparency() : Transparency.BITMASK);
		Graphics2D g = newImage.createGraphics();
		int offset = horizontally ? -1 : 1;
		g.scale(offset,-offset);
		if(horizontally)
			g.drawImage(image,-image.getWidth(null),0,null);
		else
			g.drawImage(image,0,-image.getHeight(null),null);
		g.dispose();
		return newImage;
	}
	
	/**
	 * A convenience class that buffers images and loads them all at once.
	 * @author Roi Atalla
	 */
	public class Loader implements Runnable {
		private Map<String,String> files;
		private int status;
		
		public Loader() {
			files = new HashMap<String,String>();
		}
		
		/**
		 * Returns the total number of images in this buffer.
		 * @return The total number of images in this buffer.
		 */
		public int getTotal() {
			return files.size();
		}
		
		/**
		 * Returns the number of images added and loaded.
		 * @return The number of images added and loaded.
		 */
		public int getStatus() {
			return status;
		}
		
		/**
		 * Adds the file to the buffer.
		 * @param file The file to be loaded. The associated name is its canonical name.
		 */
		public void addFile(String file) {
			addFile(file,Art.getFileName(file));
		}
		
		/**
		 * Adds the file and sets the name associated with it.
		 * @param file The file to be loaded.
		 * @param name The name to be associated to this image.
		 */
		public synchronized void addFile(String file, String name) {
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
			for(String[] s : files) {
				addFile(s[0],s[1]);
			}
		}
		
		/**
		 * Spawns a new thread and adds all images to the Art instance.
		 */
		public synchronized void start() {
			new Thread(this).start();
		}
		
		/**
		 * Adds all images to the Art instance.
		 */
		public synchronized void run() {
			for(String s : files.keySet()) {
				try{
					add(files.get(s),s);
					status++;
				}
				catch(Exception exc) {
					System.out.println(s);
					exc.printStackTrace();
					status = -1;
					return;
				}
			}
		}
	}
}