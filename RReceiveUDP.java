import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import edu.utulsa.unet.UDPSocket; //import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

import kuxhausen.networks.Packet;

public class RReceiveUDP extends RUDP implements edu.utulsa.unet.RReceiveUDPI{
	
	public static void main(String[] args)
	{
		RReceiveUDP reciever = new RReceiveUDP();
		reciever.setMode(0);
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
			while(!fin){
				byte[] buffer = new byte[mtu];
				DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
				socket.receive(packet);
				Packet p = Packet.decodePacket(buffer, packet.getLength());
				sender = new InetSocketAddress(packet.getAddress(),packet.getPort());
				if(!p.isAck){
					data.add(p.sequenceNumber, p.data);
					System.out.println(" Recieved number"+ p.sequenceNumber+" from " +packet.getAddress().getHostAddress());
					
					Packet ack = new Packet(null, p.sequenceNumber, false, true);
					socket.send(new DatagramPacket(p.toBytes(), p.toBytes().length, sender.getAddress(), sender.getPort()));
				}
			}	
			
		}
		catch(Exception e){ e.printStackTrace(); 
		}
		return false;
	}
	
}
