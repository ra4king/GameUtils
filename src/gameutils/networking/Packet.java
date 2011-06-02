package gameutils.networking;

import java.net.SocketAddress;
import java.util.ArrayList;

/**
 * A Packet holds is a container of any type of data.
 * @author Roi Atalla
 */
public class Packet {
	private SocketAddress address;
	private ArrayList<Object> messages = new ArrayList<Object>();
	private int idx = 0;
	
	/**
	 * Initializes this object. The InetSocketAddress is set to null.
	 * @param messages A variable number of data to be written to this Packet.
	 */
	public Packet(Object ... messages) {
		this(null,messages);
	}
	
	/**
	 * Initializes this object.
	 * @param address The address to connect to or which is received from.
	 * @param messages A variable number of data to be written to this Packet.
	 */
	public Packet(SocketAddress address, Object ... messages) {
		this.address = address;
		
		for(Object o : messages)
			writeObject(o);
	}
	
	/**
	 * Reads the next byte.
	 * @return The next byte.
	 */
	public byte readByte() {
		return (Byte)readObject();
	}
	
	/**
	 * Reads the next short.
	 * @return The next short.
	 */
	public short readShort() {
		return (Short)readObject();
	}
	
	/**
	 * Reads the next char.
	 * @return The next char.
	 */
	public char readChar() {
		return (Character)readObject();
	}
	
	/**
	 * Reads the next int.
	 * @return The next int.
	 */
	public int readInt() {
		return (Integer)readObject();
	}
	
	/**
	 * Reads the next long.
	 * @return The next long.
	 */
	public long readLong() {
		return (Long)readObject();
	}
	
	/**
	 * Reads the next float.
	 * @return The next float.
	 */
	public float readFloat() {
		return (Float)readObject();
	}
	
	/**
	 * Reads the next double.
	 * @return The next double.
	 */
	public double readDouble() {
		return (Double)readObject();
	}
	
	/**
	 * Reads the next boolean.
	 * @return The next boolean.
	 */
	public boolean readBoolean() {
		return (Boolean)readObject();
	}
	
	/**
	 * Reads the next String.
	 * @return The next String.
	 */
	public String readString() {
		return (String)readObject();
	}
	
	/**
	 * Reads the next Object.
	 * @return The next Object.
	 */
	public Object readObject() {
		try{
			return messages.get(idx++);
		}
		catch(IndexOutOfBoundsException exc) {
			idx--;
			throw exc;
		}
	}
	
	/**
	 * Writes a byte.
	 * @param b The byte.
	 */
	public void writeByte(byte b) {
		writeObject(b);
	}
	
	/**
	 * Writes a short.
	 * @param s The short.
	 */
	public void writeShort(short s) {
		writeObject(s);
	}
	
	/**
	 * Writes a char.
	 * @param c The char.
	 */
	public void writeChar(char c) {
		writeObject(c);
	}
	
	/**
	 * Writes an int.
	 * @param i The int.
	 */
	public void writeInt(int i) {
		writeObject(i);
	}
	
	/**
	 * Writes a long.
	 * @param l The long.
	 */
	public void writeLong(long l) {
		writeObject(l);
	}
	
	/**
	 * Writes a float.
	 * @param f The float.
	 */
	public void writeFloat(float f) {
		writeObject(f);
	}
	
	/**
	 * Writes a double.
	 * @param d The double.
	 */
	public void writeDouble(double d) {
		writeObject(d);
	}
	
	/**
	 * Writes a boolean.
	 * @param b The boolean.
	 */
	public void writeBoolean(boolean b) {
		writeObject(b);
	}
	
	/**
	 * Writes a String.
	 * @param s The String.
	 */
	public void writeString(String s) {
		writeObject(s);
	}
	
	/**
	 * Writes a Packet.
	 * @param p The Packet.
	 */
	public void writePacket(Packet p) {
		messages.addAll(p.messages);
	}
	
	/**
	 * Writes an object. Must implement java.io.Serializable or gameutils.networking.Serializable.
	 * @param o The object.
	 */
	public void writeObject(Object o) {
		if(o instanceof java.io.Serializable || o instanceof Serializable)
			messages.add(o);
		else
			throw new IllegalArgumentException("Object not serializable.");
	}
	
	/**
	 * Returns the amount of data written to this Packet.
	 * @return The amount of data written to this Packet.
	 */
	public int size() {
		return messages.size();
	}
	
	/**
	 * Returns the address to connect to or which is received from.
	 * @return The address to connect to or which is received from.
	 */
	public SocketAddress getAddress() {
		return address;
	}
	
	/**
	 * Sets the address to connect to or which is received from.
	 * @param address The new address to connect to or the one to received from.
	 */
	public void setAddress(SocketAddress address) {
		if(address == null)
			throw new IllegalArgumentException("address cannot be null");
		
		this.address = address;
	}
}