import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import edu.utulsa.unet.UDPSocket; //import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;


public class RReceiveUDP extends RUDP implements edu.utulsa.unet.RReceiveUDPI{
	
	public static void main(String[] args)
	{
		RReceiveUDP reciever = new RReceiveUDP();
		reciever.setMode(1);
		reciever.setModeParameter(512);
		reciever.setFilename("less_important.txt");
		reciever.setLocalPort(32456);
		reciever.receiveFile();
	}
	
	int reciever = 12987;
	
	public int getLocalPort() {
		return reciever;
	}

	public boolean setLocalPort(int portNum) {
		if(portNum>=0){
			reciever = portNum;
			return true;
		}
		return false;
	}
	
	InetSocketAddress sender;
	int mtu;
	ArrayList<byte[]> data = new ArrayList<byte[]>();	
	
	@Override
	public boolean receiveFile() {
		try
		{
			UDPSocket socket = new UDPSocket(getLocalPort());
			mtu = socket.getSendBufferSize();
			boolean fin = false;
			while(!fin || dataSequenceHasEmptySpots()&&fin){
				
		System.out.println("trying to recieve");		
				byte[] buffer = new byte[mtu];
				DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
				socket.receive(packet);
				
		System.out.println("recvieved something");
				
				Packet p = Packet.decodePacket(buffer, packet.getLength());
				sender = new InetSocketAddress(packet.getAddress(),packet.getPort());
				if(!p.isAck){				
					while((data.size()-1)<p.sequenceNumber)
						data.add(null);
					data.set(p.sequenceNumber, p.data);
					System.out.println("Message "+ p.sequenceNumber+" recieved with "+p.data.length+" bytes of actual data");
					
					if(p.isLast)
						fin = true;
					
					Packet ack = new Packet(new byte[0], p.sequenceNumber, fin, true);
					socket.send(new DatagramPacket(ack.toBytes(), ack.toBytes().length, sender.getAddress(), sender.getPort()));
					System.out.println("Message "+p.sequenceNumber+" acknowledgement sent");
					
				}
			}
			
			int totalBytes = 0;
			for(byte[] b :data)
				totalBytes +=b.length;
			byte[] message = new byte[totalBytes];
			ByteBuffer buf = ByteBuffer.wrap(message);
			for(byte[] b: data)
				buf.put(b);
System.out.println("message="+ new String(message));
			
		}
		catch(Exception e){ e.printStackTrace(); 
		}
		return false;
	}

	private boolean dataSequenceHasEmptySpots() {
		for(byte[] b: data)
			if(b==null)
				return true;
		return false;
	}
	
}
