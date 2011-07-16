package gameutils.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * A blocking or non-blocking UDP wrapper that uses NIO.
 * @author Roi Atalla
 */
public class NBDatagramPacketIO extends PacketIO {
	private DatagramChannel channel;
	private SocketAddress address;
	private ByteBuffer in, out;
	
	public NBDatagramPacketIO(String address, int port, boolean isBlocking) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking);
	}
	
	public NBDatagramPacketIO(String address, int port, boolean isBlocking, int bufferSize) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking,bufferSize);
	}
	
	public NBDatagramPacketIO(SocketAddress address, boolean isBlocking) throws IOException {
		this(address,isBlocking,8192);
	}
	
	public NBDatagramPacketIO(SocketAddress address, boolean isBlocking, int bufferSize) throws IOException {
		this(DatagramChannel.open(),address,bufferSize);
		
		channel.configureBlocking(isBlocking);
	}
	
	public NBDatagramPacketIO(DatagramChannel channel) {
		this(channel,null);
	}
	
	public NBDatagramPacketIO(DatagramChannel channel, int bufferSize) {
		this(channel,null,bufferSize);
	}
	
	public NBDatagramPacketIO(DatagramChannel channel, SocketAddress address) {
		this(channel,address,8192);
	}
	
	public NBDatagramPacketIO(DatagramChannel channel, SocketAddress address, int bufferSize) {
		if(!channel.isOpen())
			throw new IllegalStateException("channel is not open.");
		this.channel = channel;
		this.address = address;
		
		if(address != null) {
			try{
				channel.connect(address);
			}
			catch(Exception exc) {
				throw new IllegalArgumentException(exc.toString());
			}
		}
		
		setBufferSize(bufferSize);
	}
	
	public synchronized Packet read() throws IOException {
		in.clear();
		
		SocketAddress address = channel.receive(in);
		
		if(address == null)
			return null;
		
		in.flip();
		
		ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(in.array()));
		Packet packet = read(oin);
		packet.setAddress(address);
		return packet;
	}
	
	public synchronized boolean write(Packet packet) throws IOException {
		out.clear();
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		write(packet,new ObjectOutputStream(bout));
		
		out.put(adjustSize(bout.toByteArray()));
		out.flip();
		
		SocketAddress sa = (address == null ? packet.getAddress() : address);
		
		return channel.send(out, sa) == out.limit();
	}
	
	public synchronized void write(Packet packet, SocketAddress address) throws IOException {
		this.write(packet.setAddress(address));
	}
	
	public int getBufferSize() {
		return in.capacity();
	}
	
	public void setBufferSize(int bufferSize) {
		in = ByteBuffer.allocate(bufferSize);
		out = ByteBuffer.allocateDirect(bufferSize);
	}
	
	public void setAddress(SocketAddress address) {
		this.address = address;
	}
	
	public SocketAddress getSocketAddress() {
		return address;
	}
	
	private byte[] adjustSize(byte array[]) {
		if(array.length < out.capacity())
			return array;
		byte adjust[] = new byte[out.capacity()];
		System.arraycopy(array, 0, adjust, 0, adjust.length);
		return adjust;
	}
	
	public void close() throws IOException {
		channel.close();
	}
}