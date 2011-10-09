package gameutils.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * A blocking UDP wrapper.
 * @author Roi Atalla
 */
public class OLDDatagramPacketIO extends PacketIO {
	private DatagramSocket socket;
	private int bufferSize;
	
	/**
	 * Initializes this object. Default buffer size is 8192 bytes.
	 * @param socket The connection.
	 */
	public OLDDatagramPacketIO(DatagramSocket socket) {
		this(socket,8192);
	}
	
	/**
	 * Initializes this object.
	 * @param socket The connection.
	 * @param bufferSize The buffer size.
	 */
	public OLDDatagramPacketIO(DatagramSocket socket, int bufferSize) {
		this.socket = socket;
		this.bufferSize = bufferSize;
	}
	
	public Packet read() throws IOException {
		DatagramPacket datagram = new DatagramPacket(new byte[bufferSize],bufferSize);
		socket.receive(datagram);
		
		ByteArrayInputStream bin = new ByteArrayInputStream(datagram.getData());
		ObjectInputStream in = new ObjectInputStream(bin);
		
		Packet packet = read(in);
		packet.setAddress(datagram.getSocketAddress());
		return packet;
	}
	
	public boolean write(Packet packet) throws IOException {
		if(packet.getAddress() == null)
			throw new IllegalArgumentException("no address specified!");
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		
		write(packet,out);
		
		byte data[] = bout.toByteArray();
		
		socket.send(new DatagramPacket(data,bufferSize,packet.getAddress()));
		
		return true;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}
	
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	public InetSocketAddress getSocketAddress() {
		return (InetSocketAddress)socket.getRemoteSocketAddress();
	}
	
	public boolean isConnected() {
		return socket.isConnected();
	}
	
	public void close() throws IOException {
		socket.close();
	}
}