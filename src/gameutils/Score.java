package gameutils;

/**
 * Encrypts the score so hackers can't modify the memory.
 * @author Roi Atalla
 */
public class Score {
	private long score;
	private long highscore;
	private long mask;
	
	/**
	 * Default constructor.
	 */
	public Score() {
		this(0);
	}
	
	/**
	 * Initializes this object.
	 * @param score The score to be encrypted.
	 */
	public Score(long score) {
		this(score,System.nanoTime());
	}
	
	/**
	 * Initializes this object.
	 * @param score The current score to be encrypted.
	 * @param mask The mask to be applied to the score.
	 */
	public Score(long score, long mask) {
		this.mask = mask;
		this.score = encrypt(score);
		highscore = encrypt(0);
	}
	
	/**
	 * Adds the specified points to the score.
	 * @param points The points to add to the score.
	 */
	public void add(long points) {
		score = encrypt(decrypt(score)+points);
	}
	
	/**
	 * Returns the current score.
	 * @return
	 */
	public long get() {
		return decrypt(score);
	}
	
	/**
	 * Sets the current score as the high score.
	 */
	public void setHighScore() {
		highscore = score;
	}
	
	/**
	 * Sets the specified score as the high score.
	 * @param hscore The score to be set as the high score.
	 */
	public void setHighScore(int hscore) {
		highscore = encrypt(hscore);
	}
	
	/**
	 * Returns the high score.
	 * @return The high score.
	 */
	public long getHighScore() {
		return decrypt(highscore);
	}
	
	/**
	 * Resets the score back to 0.
	 */
	public void reset() {
		score = encrypt(0);
	}
	
	private long encrypt(long num) {
		return num^mask;
	}
	
	private long decrypt(long num) {
		return num^mask;
	}
}