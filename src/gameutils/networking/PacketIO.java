package gameutils.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;

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
	 * This is the standard way of reading a Packet from an ObjectInputStream. If the protocol used only returns byte arrays, it is recommended to wrap the array into a ByteArrayInputStream and then wrap that into an ObjectInputStream.
	 * @param in The ObjectInputStream to read the data from.
	 * @return The Packet with the data read.
	 * @throws IOException
	 */
	protected Packet read(ObjectInputStream in) throws IOException {
		int length = in.readInt();
		
		Packet packet = new Packet();
		
		for(int a = 0; a < length; a++) {
			byte id = in.readByte();
			switch(id) {
				case 0: packet.writeByte(in.readByte()); break;
				case 1: packet.writeShort(in.readShort()); break;
				case 2: packet.writeChar(in.readChar()); break;
				case 3: packet.writeInt(in.readInt()); break;
				case 4: packet.writeLong(in.readLong()); break;
				case 5: packet.writeFloat(in.readFloat()); break;
				case 6: packet.writeDouble(in.readDouble()); break;
				case 7: packet.writeBoolean(in.readBoolean()); break;
				case 8: packet.writeString(in.readUTF()); break;
				case 9:
					try {
						String alias = in.readUTF();
						Class<?> clazz = AliasManager.getClass(alias);
						if(clazz == null)
							throw new IOException("Class not found!");
						
						Serializable s = (Serializable)clazz.newInstance();
						s.deserialize(read(in));
						packet.writeObject(s);
						break;
					}
					catch(InstantiationException exc) {
						throw new IOException("No default constructor!");
					}
					catch(Exception exc) {
						throw new IOException(exc.toString());
					}
				case 10:
					try{
						packet.writeObject(in.readUnshared());
						break;
					}
					catch(Exception exc) {
						throw new IOException(exc.toString());
					}
				default: throw new IOException("Invalid type ID: " + id);
			}
		}
		
		return packet;
	}
	
	/**
	 * Writes a packet to the underlying connection.
	 * @param packet The Packet to be written.
	 * @throws IOException
	 */
	public abstract boolean write(Packet packet) throws IOException;
	
	/**
	 * This is the standard way of writing a Packet to an ObjectOutputStream. If the protocol used only accepts byte arrays, it is recommended to wrap a ByteArrayOutputStream into an ObjectOutputStream and getting the byte array data from there.
	 * @param packet The Packet with the data to write.
	 * @param out The ObjectOutputStream to write the data to.
	 * @throws IOException
	 */
	protected void write(Packet packet, ObjectOutputStream out) throws IOException {
		out.writeInt(packet.size());
		out.flush();
		
		for(int a = 0; a < packet.size(); a++) {
			Object o = packet.readObject();
			if(o instanceof Byte) {
				out.writeByte(0);
				out.writeByte((Byte)o);
			}
			else if(o instanceof Short) {
				out.writeByte(1);
				out.writeShort((Short)o);
			}
			else if(o instanceof Character) {
				out.writeByte(2);
				out.writeChar((Character)o);
			}
			else if(o instanceof Integer) {
				out.writeByte(3);
				out.writeInt((Integer)o);
			}
			else if(o instanceof Long) {
				out.writeByte(4);
				out.writeLong((Long)o);
			}
			else if(o instanceof Float) {
				out.writeByte(5);
				out.writeFloat((Float)o);
			}
			else if(o instanceof Double) {
				out.writeByte(6);
				out.writeDouble((Double)o);
			}
			else if(o instanceof Boolean) {
				out.writeByte(7);
				out.writeBoolean((Boolean)o);
			}
			else if(o instanceof String) {
				out.writeByte(8);
				out.writeUTF((String)o);
			}
			else if(o instanceof Serializable) {
				out.writeByte(9);
				
				String alias = AliasManager.getAlias(o.getClass());
				if(alias == null)
					alias = AliasManager.register((Serializable)o);
				
				out.writeUTF(alias);
				Packet p = new Packet();
				((Serializable)o).serialize(p);
				write(p,out);
			}
			else {
				out.writeByte(10);
				out.writeUnshared(o);
			}
			
			out.flush();
		}
	}
	
	/**
	 * Returns the IP address of the remote connection.
	 * @return The IP address of the remote connection.
	 */
	public abstract SocketAddress getSocketAddress();
	
	/**
	 * Closes this connection.
	 * @throws IOException
	 */
	public abstract void close() throws IOException;
}