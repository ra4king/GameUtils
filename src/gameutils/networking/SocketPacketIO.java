package gameutils.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * A blocking TCP wrapper.
 * @author Roi Atalla
 */
public class SocketPacketIO extends PacketIO {
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	
	public SocketPacketIO(String address, int port) throws IOException {
		this(new Socket(address,port));
	}
	
	/**
	 * Initializes this object.
	 * @param socket The connection.
	 * @throws IOException
	 */
	public SocketPacketIO(Socket socket) throws IOException {
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		
		this.socket = socket;
	}
	
	/**
	 * Reads a Packet from the underlying connection. The remote SocketAddress is added to the Packet.
	 */
	public Packet read() throws IOException {
		Packet packet = read(in);
		packet.setAddress(socket.getRemoteSocketAddress());
		return packet;
	}
	
	/**
	 * Writes a packet to the underlying connection. If an address is specified, it is ignored.
	 */
	public boolean write(Packet packet) throws IOException {
		write(packet,out);
		return true;
	}
	
	/**
	 * Returns the Socket used in this connection.
	 * @return The Socket used in this connection.
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * Returns the InputStream used in this connection. In this case it is an ObjectInputStream
	 * @return The InputStream used in this connection.
	 * @throws IOException
	 */
	public InputStream getInputStream() {
		return in;
	}
	
	/**
	 * Returns the OutputStream used in this connection. In this case it is an ObjectOutputStream.
	 * @return The OutputStream used in this connection.
	 */
	public OutputStream getOutputStream() {
		return out;
	}
	
	public SocketAddress getSocketAddress() {
		return socket.getRemoteSocketAddress();
	}
	
	public String getHostAddress() {
		return socket.getInetAddress().getHostAddress();
	}
	
	public void close() throws IOException {
		socket.close();
	}
}