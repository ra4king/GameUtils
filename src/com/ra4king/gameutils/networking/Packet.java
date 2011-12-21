package com.ra4king.gameutils.networking;

import java.net.SocketAddress;
import java.util.ArrayList;

/**
 * A Packet is a container of any type of data.
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
	 * Reads the next integer.
	 * @return The next integer.
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
	 * Peeks the next Object. The current position isn't incremented.
	 * @return The next Object.
	 */
	public Object peek() {
		return messages.get(idx);
	}
	
	public Packet slice(int start) {
		if(start > messages.size())
			throw new IndexOutOfBoundsException();
		
		Packet packet = new Packet();
		packet.messages.addAll(start,messages);
		return packet;
	}
	
	public Packet slice(int start, int end) {
		if(start > messages.size() || end > messages.size())
			throw new IndexOutOfBoundsException();
		
		Packet packet = new Packet();
		for(; start < end; start++)
			packet.messages.add(messages.get(start));
		return packet;
	}
	
	/**
	 * Writes a byte.
	 * @param b The byte.
	 * @return Returns this.
	 */
	public Packet writeByte(byte b) {
		writeObject(b);
		return this;
	}
	
	/**
	 * Writes a short.
	 * @param s The short.
	 * @return Returns this.
	 */
	public Packet writeShort(short s) {
		writeObject(s);
		return this;
	}
	
	/**
	 * Writes a char.
	 * @param c The char.
	 * @return Returns this.
	 */
	public Packet writeChar(char c) {
		writeObject(c);
		return this;
	}
	
	/**
	 * Writes an integer.
	 * @param i The integer.
	 * @return Returns this.
	 */
	public Packet writeInt(int i) {
		writeObject(i);
		return this;
	}
	
	/**
	 * Writes a long.
	 * @param l The long.
	 * @return Returns this.
	 */
	public Packet writeLong(long l) {
		writeObject(l);
		return this;
	}
	
	/**
	 * Writes a float.
	 * @param f The float.
	 * @return Returns this.
	 */
	public Packet writeFloat(float f) {
		writeObject(f);
		return this;
	}
	
	/**
	 * Writes a double.
	 * @param d The double.
	 * @return Returns this.
	 */
	public Packet writeDouble(double d) {
		writeObject(d);
		return this;
	}
	
	/**
	 * Writes a boolean.
	 * @param b The boolean.
	 * @return Returns this.
	 */
	public Packet writeBoolean(boolean b) {
		writeObject(b);
		return this;
	}
	
	/**
	 * Writes a String.
	 * @param s The String.
	 * @return Returns this.
	 */
	public Packet writeString(String s) {
		writeObject(s);
		return this;
	}
	
	/**
	 * Writes a Packet.
	 * @param p The Packet.
	 * @return Returns this.
	 */
	public Packet writePacket(Packet p) {
		messages.addAll(p.messages);
		return this;
	}
	
	/**
	 * Writes an object. Must implement java.io.Serializable or gameutils.networking.Serializable.
	 * @param o The object.
	 * @return Returns this.
	 */
	public Packet writeObject(Object o) {
		if(o instanceof java.io.Serializable)
			messages.add(o);
		else
			throw new IllegalArgumentException("Object not serializable.");
		
		return this;
	}
	
	/**
	 * Absolute get method
	 * @param index The index of the object
	 * @return The object at the specified index.
	 */
	public Object get(int index) {
		return messages.get(index);
	}
	
	/**
	 * Absolute set method
	 * @param index The index of the object
	 * @param o The object to set 
	 * @return Returns this.
	 */
	public Packet set(int index, Object o) {
		messages.set(index,o);
		return this;
	}
	
	/**
	 * Resets the position but does not clear the buffer.
	 * @return Returns this.
	 */
	public Packet reset() {
		idx = 0;
		return this;
	}
	
	/**
	 * Clears the entire buffer and resets the Packet.
	 * @return Returns this.
	 */
	public Packet clear() {
		messages.clear();
		reset();
		return this;
	}
	
	/**
	 * Returns true if there are more messages to read, false otherwise.
	 * @return True if there are more messages to read, false otherwise.
	 */
	public boolean hasMore() {
		return idx < size();
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
	 * @return Returns this.
	 */
	public Packet setAddress(SocketAddress address) {
		if(address == null)
			throw new IllegalArgumentException("address cannot be null");
		
		this.address = address;
		
		return this;
	}
}