package gameutils.networking;

/**
 * All classes that will write their object data on a connection must extend this class.
 * This class is more suitable for those that are in different packages.
 * @author Roi Atalla
 */
public interface Serializable {
	/**
	 * A class should write its object data into the Packet.
	 * @param p The Packet to write into.
	 */
	public void serialize(Packet p);
	
	/**
	 * A class should read its object data from the Packet.
	 * @param p The Packet to read from.
	 */
	public void deserialize(Packet p);
}