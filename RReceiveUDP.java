import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import edu.utulsa.unet.UDPSocket; //import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import kuxhausen.networks.Packet;

public class RReceiveUDP extends RUDP implements edu.utulsa.unet.RReceiveUDPI{
	static final int PORT = 32456;
	
	public static void main(String[] args)
	{
		RReceiveUDP reciever = new RReceiveUDP();
		reciever.setMode(2);
		reciever.setModeParameter(512);
		reciever.setFilename("less_important.txt");
		reciever.setLocalPort(32456);
		reciever.receiveFile();
	}
	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean setLocalPort(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	Mode mode = Mode.StopAndWait;
	long slidingWindowSize = 0;
	/** in miliseconds **/
	long timeOut = 1000;
	int sendPort = 12987;
	InetSocketAddress reciever;
	String filename;
	
	@Override
	public boolean receiveFile() {
		try
		{
			byte [] buffer = new byte[25];
			UDPSocket socket = new UDPSocket(PORT);
			DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
			socket.receive(packet);
			
			InetAddress client = packet.getAddress();
			System.out.println(" Received'"+new String(buffer)+"' from " +packet.getAddress().getHostAddress());
			System.out.println(" Received'"+new String(Arrays.copyOf(buffer, packet.getLength()))+"' from " +packet.getAddress().getHostAddress());
		}
		catch(Exception e){ e.printStackTrace(); 
		}
		return false;
	}
	
}
