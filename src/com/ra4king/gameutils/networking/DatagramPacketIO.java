package com.ra4king.gameutils.networking;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class DatagramPacketIO extends PacketIO {
	private DatagramChannel channel;
	private InetSocketAddress address;
	private ByteBuffer in, out;
	
	public DatagramPacketIO(String address, int port, boolean isBlocking) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking);
	}
	
	public DatagramPacketIO(String address, int port, int bufferSize) throws IOException {
		this(new InetSocketAddress(address,port),true,bufferSize);
	}
	
	public DatagramPacketIO(String address, int port, boolean isBlocking, int bufferSize) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking,bufferSize);
	}
	
	public DatagramPacketIO(InetSocketAddress address) throws IOException {
		this(address,true);
	}
	
	public DatagramPacketIO(InetSocketAddress address, boolean isBlocking) throws IOException {
		this(address,isBlocking,1024);
	}
	
	public DatagramPacketIO(InetSocketAddress address, int bufferSize) throws IOException {
		this(address,true,bufferSize);
	}
	
	public DatagramPacketIO(InetSocketAddress address, boolean isBlocking, int bufferSize) throws IOException {
		this(DatagramChannel.open(),address,bufferSize);
	}
	
	public DatagramPacketIO(DatagramChannel channel) throws IOException {
		this(channel,null);
	}
	
	public DatagramPacketIO(DatagramChannel channel, boolean isBlocking) throws IOException {
		this(channel,null,isBlocking);
	}
	
	public DatagramPacketIO(DatagramChannel channel, int bufferSize) throws IOException {
		this(channel,true,bufferSize);
	}
	
	public DatagramPacketIO(DatagramChannel channel, boolean isBlocking, int bufferSize) throws IOException {
		this(channel,null,isBlocking,bufferSize);
	}
	
	public DatagramPacketIO(DatagramChannel channel, InetSocketAddress address) throws IOException {
		this(channel,address,true);
	}
	
	public DatagramPacketIO(DatagramChannel channel, InetSocketAddress address, boolean isBlocking) throws IOException {
		this(channel,address,isBlocking,1024);
	}
	
	public DatagramPacketIO(DatagramChannel channel, InetSocketAddress address, int bufferSize) throws IOException {
		this(channel,address,true,bufferSize);
	}
	
	public DatagramPacketIO(DatagramChannel channel, InetSocketAddress address, boolean isBlocking, int bufferSize) throws IOException {
		if(!channel.isOpen())
			throw new IllegalStateException("channel is not open.");
		
		this.channel = channel;
		this.address = address;
		
		channel.configureBlocking(isBlocking);
		
		if(address != null)
			channel.connect(address);
		
		setBufferSize(bufferSize);
	}
	
	public Packet read() throws IOException {
		final ByteBuffer in = this.in;
		
		in.clear();
		
		SocketAddress address = channel.receive(in);
		
		if(address == null)
			return null;
		
		in.flip();
		
		ObjectInputStream oin = new ObjectInputStream(new InputStream() {
			public int read() {
				if(!in.hasRemaining())
					return -1;
				
				return in.get() & 0xff;
			}
		});
		
		Packet packet = read(oin);
		packet.setAddress(address);
		return packet;
	}
	
	public synchronized boolean write(Packet packet) throws IOException {
		ByteBuffer out = this.out;
		
		out.clear();
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		write(packet,new ObjectOutputStream(bout));
		
		out.put(adjustSize(bout.toByteArray()));
		out.flip();
		
		SocketAddress sa = (packet.getAddress() == null ? address : packet.getAddress());
		
		if(sa == null)
			throw new IOException("No address specified.");
		
		return channel.send(out, sa) > 0;
	}
	
	public void write(Packet packet, SocketAddress address) throws IOException {
		write(packet.setAddress(address));
	}
	
	public int getBufferSize() {
		return in.capacity();
	}
	
	public void setBufferSize(int bufferSize) {
		in = ByteBuffer.allocateDirect(bufferSize);
		out = ByteBuffer.allocateDirect(bufferSize);
	}
	
	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}
	
	public InetSocketAddress getSocketAddress() {
		return address;
	}
	
	private byte[] adjustSize(byte array[]) {
		if(array.length < out.capacity())
			return array;
		byte adjust[] = new byte[out.capacity()];
		System.arraycopy(array, 0, adjust, 0, adjust.length);
		return adjust;
	}
	
	public boolean isConnected() {
		return channel.socket().isClosed();
	}
	
	public void close() throws IOException {
		channel.close();
	}
}
