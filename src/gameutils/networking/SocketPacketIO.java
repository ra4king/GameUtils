package gameutils.networking;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * A Non-Blocking TCP wrapper.
 * @author Roi Atalla
 */
public class SocketPacketIO extends PacketIO {
	private SocketChannel channel;
	private ByteBuffer in, out;
	
	public SocketPacketIO(String address, int port) throws IOException {
		this(address,port,true);
	}
	
	public SocketPacketIO(String address, int port, boolean isBlocking) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking);
	}
	
	public SocketPacketIO(String address, int port, boolean isBlocking, int bufferSize) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking,bufferSize);
	}
	
	public SocketPacketIO(String address, int port, int bufferSize) throws IOException {
		this(address,port,false,bufferSize);
	}
	
	public SocketPacketIO(SocketAddress address, boolean isBlocking) throws IOException {
		this(SocketChannel.open(address),isBlocking);
	}
	
	public SocketPacketIO(SocketAddress address, boolean isBlocking, int bufferSize) throws IOException {
		this(SocketChannel.open(address),isBlocking,bufferSize);
	}
	
	public SocketPacketIO(SocketChannel channel) throws IOException {
		this(channel,true);
	}
	
	public SocketPacketIO(SocketChannel channel, boolean isBlocking) throws IOException {
		this(channel,isBlocking,8192);
	}
	
	public SocketPacketIO(SocketChannel channel, int bufferSize) throws IOException {
		this(channel,false,bufferSize);
	}
	
	public SocketPacketIO(SocketChannel channel, boolean isBlocking, int bufferSize) throws IOException {
		if(!channel.isOpen())
			throw new IllegalStateException("channel is not open.");
		
		this.channel = channel;
		
		channel.configureBlocking(isBlocking);
		
		try{
			channel.socket().setTcpNoDelay(true);
		}
		catch(Exception exc) {
			throw new IOException(exc);
		}
		
		setBufferSize(bufferSize);
	}
	
	public Packet read() throws IOException {
		final ByteBuffer in = this.in;
		
		if(isBlocking()) {
			do {
				if(channel.read(in) <= 0)
					throw new IOException("Connection is closed.");
				
				if(in.position() <= 2)
					return read();
			}while(in.position()+2 < in.getShort(0));
		}
		else {
			if(channel.read(in) <= 0 || (in.position() >= 2 && in.position()+2 < in.getShort(0)))
				return null;
		}
		
		in.flip();
		
		short length = in.getShort();
		if(in.remaining() < length)
			throw new IOException("Internal Error!!");
		
		ObjectInputStream oin = new ObjectInputStream(new InputStream() {
			public int read() {
				if(!in.hasRemaining())
					return -1;
				
				return (int)in.get() & 0xff;
			}
		});
		
		Packet packet = read(oin);
		packet.setAddress(getSocketAddress());
		
		if(in.remaining() > 0)
			in.compact();
		else
			in.clear();
		
		return packet;
	}
	
	public boolean write(Packet packet) throws IOException {
		ByteBuffer out = this.out;
		
		out.clear();
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		write(packet,new ObjectOutputStream(bout));
		
		byte[] array = adjustSize(bout.toByteArray());
		
		out.putShort((short)array.length);
		out.put(array);
		out.flip();
		
		return channel.write(out) > 0;
	}
	
	public int getBufferSize() {
		return in.capacity();
	}
	
	public void setBufferSize(int bufferSize) {
		in = ByteBuffer.allocateDirect(bufferSize);
		out = ByteBuffer.allocateDirect(bufferSize);
	}
	
	public boolean isBlocking() {
		return channel.isBlocking();
	}
	
	public void setBlocking(boolean isBlocking) throws IOException {
		channel.configureBlocking(isBlocking);
	}
	
	public void setSocketAddress(SocketAddress address) {
		try {
			channel.connect(address);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public InetSocketAddress getSocketAddress() {
		return (InetSocketAddress)channel.socket().getRemoteSocketAddress();
	}
	
	private byte[] adjustSize(byte array[]) {
		if(array.length <= out.capacity())
			return array;
		
		byte adjust[] = new byte[out.capacity()];
		System.arraycopy(array, 0, adjust, 0, adjust.length);
		return adjust;
	}
	
	public boolean isConnected() {
		return !channel.socket().isClosed();
	}
	
	public void close() throws IOException {
		channel.socket().shutdownInput();
		channel.socket().shutdownOutput();
		channel.close();
	}
}
