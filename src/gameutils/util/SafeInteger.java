package gameutils.util;

/**
 * Stores and XORs an integer.
 * @author Roi Atalla
 */
public class SafeInteger {
	private int value;
	private int mask;
	
	/**
	 * Sets the value to 0 and chooses a random mask.
	 */
	public SafeInteger() {
		this(0);
	}
	
	/**
	 * Sets the value and chooses a random mask.
	 * @param value The value to be set.
	 */
	public SafeInteger(int value) {
		this(value,(int)(Math.random()*Integer.MAX_VALUE));
	}
	
	/**
	 * Sets the value and the mask.
	 * @param value The value to be set.
	 * @param mask The mask to be used.
	 */
	public SafeInteger(int value, int mask) {
		this.mask = mask;
		this.value = encrypt(value);
	}
	
	/**
	 * Returns the integer.
	 * @return The integer.
	 */
	public int get() {
		return decrypt(value);
	}
	
	/**
	 * Sets an integer.
	 * @param value The integer to be set.
	 */
	public void set(int value) {
		this.value = encrypt(value);
	}
	
	private int encrypt(int a) {
		return (int)(a ^ mask);
	}
	
	private int decrypt(int a) {
		return a ^ mask;
	}
}
