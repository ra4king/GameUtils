package com.ra4king.gameutils.networking;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * An abstract class for all types of connections:<br/>
 * TCP: SocketPacketIO<br/>
 * UDP: DatagramPacketIO<br/>
 * @author Roi Atalla
 */
public abstract class PacketIO {
	/**
	 * Reads a Packet from the underlying connection.
	 * @return The packet read.
	 * @throws IOException
	 */
	public abstract Packet read() throws IOException;
	
	/**
	 * Writes a packet to the underlying connection.
	 * @param packet The Packet to be written.
	 * @throws IOException
	 */
	public abstract boolean write(Packet packet) throws IOException;
	
	/**
	 * Returns the SocketAddress of the remote connection.
	 * @return The SocketAddress of the remote connection.
	 */
	public abstract InetSocketAddress getSocketAddress();
	
	/**
	 * Closes this connection.
	 * @throws IOException
	 */
	public abstract void close() throws IOException;
	
	/**
	 * Returns true if there is a connection still available.
	 * @return true if there is a connection.
	 */
	public abstract boolean isConnected();
}