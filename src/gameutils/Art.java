package gameutils;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * A class that stores Images.
 * @author Roi Atalla
 */
public class Art extends Assets<Image> {
	public Image add(Image i, String name) {
		return super.add(createCompatibleImage(i),name);
	}
	
	public Image extract(URL url) throws IOException {
		return ImageIO.read(url);
	}
	
	Art() {}
	
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
				add(i2,"Image"+size());
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
}