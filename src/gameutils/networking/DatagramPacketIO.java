package gameutils.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * A blocking UDP wrapper.
 * @author Roi Atalla
 */
public class DatagramPacketIO extends PacketIO {
	private DatagramSocket socket;
	
	/**
	 * Initializes this object.
	 * @param socket The connection.
	 * @throws IOException
	 */
	public DatagramPacketIO(DatagramSocket socket) throws IOException {
		this.socket = socket;
	}
	
	private int toInt(byte data[]) {
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
	}
	
	public Packet read() throws IOException {
		DatagramPacket datagram = new DatagramPacket(new byte[4],4);
		socket.receive(datagram);
		
		int byteLength = toInt(datagram.getData());
		
		datagram = new DatagramPacket(new byte[byteLength],byteLength);
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
		
		socket.send(new DatagramPacket(toByteArray(data.length),4,packet.getAddress()));
		
		socket.send(new DatagramPacket(data,data.length,packet.getAddress()));
	}
	
	public String getHostAddress() {
		return socket.getInetAddress().getHostAddress();
	}
	
	public void close() throws IOException {
		socket.close();
	}
}