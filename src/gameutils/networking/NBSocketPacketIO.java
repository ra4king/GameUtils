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
public class NBSocketPacketIO extends PacketIO {
	private SocketChannel channel;
	private ByteBuffer in, out;
	
	public NBSocketPacketIO(String address, int port, boolean isBlocking) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking);
	}
	
	public NBSocketPacketIO(String address, int port, boolean isBlocking, int bufferSize) throws IOException {
		this(new InetSocketAddress(address,port),isBlocking,bufferSize);
	}
	
	public NBSocketPacketIO(SocketAddress address, boolean isBlocking) throws IOException {
		this(address,isBlocking,8192);
	}
	
	public NBSocketPacketIO(SocketAddress address, boolean isBlocking, int bufferSize) throws IOException {
		this(SocketChannel.open(address),bufferSize);
		
		channel.configureBlocking(isBlocking);
	}
	
	public NBSocketPacketIO(SocketChannel channel) {
		this(channel,8192);
	}
	
	public NBSocketPacketIO(SocketChannel channel, int bufferSize) {
		if(!channel.isOpen())
			throw new IllegalStateException("channel is not open.");
		this.channel = channel;
		
		setBufferSize(bufferSize);
	}
	
	public Packet read() throws IOException {
		in.clear();
		
		if(channel.read(in) <= 0)
			return null;
		
		in.flip();
		
		ObjectInputStream oin = new ObjectInputStream(new InputStream() {
			public int read() throws IOException {
				if(!in.hasRemaining())
					return -1;
				
				int b = in.get();
				if(b < 0) {
					b &= 255;
					b |= 128;
				}
				return b;
			}
		});
		
		Packet packet = read(oin);
		packet.setAddress(getSocketAddress());
		return packet;
	}
	
	public boolean write(Packet packet) throws IOException {
		out.clear();
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		write(packet,new ObjectOutputStream(bout));
		
		out.put(adjustSize(bout.toByteArray()));
		out.flip();
		
		return channel.write(out) == out.limit();
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
	
	public InetSocketAddress getSocketAddress() {
		return (InetSocketAddress)channel.socket().getRemoteSocketAddress();
	}
	
	private byte[] adjustSize(byte array[]) {
		if(array.length <= out.capacity())
			return array;
		System.out.println("ADJUSTING SIZE!");
		byte adjust[] = new byte[out.capacity()];
		System.arraycopy(array, 0, adjust, 0, adjust.length);
		return adjust;
	}
	
	public void close() throws IOException {
		channel.close();
	}
}
