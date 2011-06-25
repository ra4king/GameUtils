package gameutils.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * A blocking UDP wrapper.
 * @author Roi Atalla
 */
public class DatagramSocketPacketIO extends PacketIO {
	private DatagramSocket socket;
	private int bufferSize;
	
	/**
	 * Initializes this object. Default buffer size is 8192 bytes.
	 * @param socket The connection.
	 */
	public DatagramSocketPacketIO(DatagramSocket socket) {
		this(socket,8192);
	}
	
	/**
	 * Initializes this object.
	 * @param socket The connection.
	 * @param bufferSize The buffer size.
	 */
	public DatagramSocketPacketIO(DatagramSocket socket, int bufferSize) {
		this.socket = socket;
		this.bufferSize = bufferSize;
	}
	
	/*private int toInt(byte data[]) {
		if(data.length != 4)
			throw new IllegalArgumentException("Size of data array is not 4");
		
		int value = 0;
		for(int a = 0; a < 4; a++)
			value = (value << 8) + (data[a] & 0xff);
		
		return value;
	}
	
	private byte[] toByteArray(int data) {
		return new byte[] {
				(byte)(data >>> 24),
				(byte)(data >>> 16),
				(byte)(data >>> 8),
				(byte) data };
	}*/
	
	public Packet read() throws IOException {
		DatagramPacket datagram = new DatagramPacket(new byte[bufferSize],bufferSize);
		socket.receive(datagram);
		
		ByteArrayInputStream bin = new ByteArrayInputStream(datagram.getData());
		ObjectInputStream in = new ObjectInputStream(bin);
		
		Packet packet = read(in);
		packet.setAddress(datagram.getSocketAddress());
		return packet;
	}
	
	public void write(Packet packet) throws IOException {
		if(packet.getAddress() == null)
			throw new IllegalArgumentException("no address specified!");
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		
		write(packet,out);
		
		byte data[] = bout.toByteArray();
		
		socket.send(new DatagramPacket(data,bufferSize,packet.getAddress()));
	}
	
	public int getBufferSize() {
		return bufferSize;
	}
	
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	public SocketAddress getSocketAddress() {
		return socket.getRemoteSocketAddress();
	}
	
	public void close() throws IOException {
		socket.close();
	}
}