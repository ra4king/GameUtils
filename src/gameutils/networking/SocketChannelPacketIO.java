package gameutils.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelPacketIO extends PacketIO {
	private SocketChannel channel;
	private ByteBuffer in, out;
	
	public SocketChannelPacketIO(String address, int port, boolean isBlocking) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking);
	}
	
	public SocketChannelPacketIO(String address, int port, boolean isBlocking, int bufferSize) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking,bufferSize);
	}
	
	public SocketChannelPacketIO(SocketAddress address, boolean isBlocking) throws IOException {
		this(address,isBlocking,8192);
	}
	
	public SocketChannelPacketIO(SocketAddress address, boolean isBlocking, int bufferSize) throws IOException {
		this(SocketChannel.open(address),bufferSize);
		
		channel.configureBlocking(isBlocking);
	}
	
	public SocketChannelPacketIO(SocketChannel channel) {
		this(channel,8192);
	}
	
	public SocketChannelPacketIO(SocketChannel channel, int bufferSize) {
		if(!channel.isOpen())
			throw new IllegalStateException("channel is not open.");
		this.channel = channel;
		
		setBufferSize(bufferSize);
	}
	
	public synchronized Packet read() throws IOException {
		in.clear();
		
		if(channel.read(in) <= 0)
			return null;
		
		in.flip();
		
		ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(in.array()));
		Packet packet = read(oin);
		packet.setAddress(getSocketAddress());
		return packet;
	}
	
	public synchronized void write(Packet packet) throws IOException {
		out.clear();
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		write(packet,new ObjectOutputStream(bout));
		
		out.put(adjustSize(bout.toByteArray()));
		out.flip();
		
		channel.write(out);
	}
	
	public int getBufferSize() {
		return in.capacity();
	}
	
	public void setBufferSize(int bufferSize) {
		in = ByteBuffer.allocate(bufferSize);
		out = ByteBuffer.allocateDirect(bufferSize);
	}
	
	public boolean isBlocking() {
		return channel.isBlocking();
	}
	
	public synchronized void setBlocking(boolean isBlocking) throws IOException {
		channel.configureBlocking(isBlocking);
	}
	
	public synchronized void setSocketAddress(SocketAddress address) {
		try {
			channel.connect(address);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public SocketAddress getSocketAddress() {
		return channel.socket().getRemoteSocketAddress();
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
