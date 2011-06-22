package gameutils.util;

/**
 * Stores and XORs a long.
 * @author Roi Atalla
 */
public class SafeLong {
	private long value;
	private long mask;
	
	/**
	 * Sets the value to 0 and chooses a random mask.
	 */
	public SafeLong() {
		this(0);
	}
	
	/**
	 * Sets the value and chooses a random mask.
	 * @param value The value to be set.
	 */
	public SafeLong(long value) {
		this(value,System.nanoTime());
	}
	
	/**
	 * Sets the value and the mask.
	 * @param value The value to be set.
	 * @param mask The mask to be used.
	 */
	public SafeLong(long value, long mask) {
		this.mask = mask;
		this.value = encrypt(value);
	}
	
	/**
	 * Returns the long.
	 * @return The long.
	 */
	public long get() {
		return decrypt(value);
	}
	
	/**
	 * Sets a long.
	 * @param value The long to be set.
	 */
	public void set(long value) {
		this.value = encrypt(value);
	}
	
	private long encrypt(long a) {
		return (int)(a ^ mask);
	}
	
	private long decrypt(long a) {
		return a ^ mask;
	}
}
