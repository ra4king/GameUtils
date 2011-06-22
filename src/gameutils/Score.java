package gameutils;

import gameutils.util.FastMath;

/**
 * Stores the score and high score.
 * @author Roi Atalla
 */
public class Score {
	private double score;
	private long highscore;
	
	/**
	 * Default constructor. Sets the score to 0.
	 */
	public Score() {
		this(0);
	}
	
	/**
	 * Initializes this object.
	 * @param score The score to store.
	 */
	public Score(double score) {
		this.score = score;
	}
	
	/**
	 * Adds the specified points to the score.
	 * @param points The points to add to the score.
	 */
	public void add(double points) {
		score += points;
	}
	
	/**
	 * Returns the score.
	 * @return The score.
	 */
	public double get() {
		return score;
	}
	
	/**
	 * Returns the score rounded to an integer.
	 * @return The score rounded to an integer.
	 */
	public int getInt() {
		return (int)getLong();
	}
	
	/**
	 * Returns the score rounded to a long.
	 * @return The score rounded to a long.
	 */
	public long getLong() {
		return FastMath.round(get());
	}
	
	/**
	 * Sets the score.
	 * @param score The score to be set.
	 */
	public void set(long score) {
		this.score = score;
	}
	
	/**
	 * Rounds the score to a long and sets it as the high score.
	 */
	public void setHighScore() {
		highscore = getLong();
	}
	
	/**
	 * Sets the specified score as the high score.
	 * @param highscore The score to be set as the high score.
	 */
	public void setHighScore(long highscore) {
		this.highscore = highscore;
	}
	
	/**
	 * Returns the high score.
	 * @return The high score.
	 */
	public long getHighScore() {
		return highscore;
	}
	
	/**
	 * Resets the score and high score back to 0.
	 */
	public void reset() {
		score = highscore = 0;
	}
}