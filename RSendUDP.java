import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import edu.utulsa.unet.UDPSocket; //import java.net.DatagramSocket;
import java.net.InetAddress;

public class RSendUDP extends RUDP implements edu.utulsa.unet.RSendUDPI{
	
	static final String SERVER = "localhost";
	static final int PORT = 32456;

	public static void main(String[] args)
	{
		RSendUDP sender = new RSendUDP();
		sender.setMode(2);
		sender.setModeParameter(512);
		sender.setTimeout(10000);
		sender.setFilename("important.txt");
		sender.setLocalPort(23456);
		sender.setReceiver(new InetSocketAddress("172.17.34.56",32456));
		sender.sendFile();
	}

	/** in miliseconds **/
	long timeOut = 1000;
	int sendPort = 12987;
	InetSocketAddress reciever;
	
	public int getLocalPort() {
		return sendPort;
	}

	public InetSocketAddress getReceiver() {
		return reciever;
	}

	
	public long getTimeout() {
		return timeOut;
	}	

	public boolean setLocalPort(int portNum) {
		if(portNum>=0){
			sendPort = portNum;
			return true;
		}
		return false;
	}
	
	public boolean setReceiver(InetSocketAddress address) {
		reciever = address;
		return true;
	}
	
	public boolean setTimeout(long time) {
		timeOut = time;
		return true;
	}
	
	
	@Override
	public boolean sendFile() {
		try {

			byte [] buffer = ("Hello World").getBytes();
			UDPSocket socket = new UDPSocket();
			socket.send(new DatagramPacket(buffer, buffer.length,
 				InetAddress.getByName(SERVER), PORT));
		}
			catch(Exception e){ e.printStackTrace();
		}

		return false;
	}
	
	private byte[] getMessage(){
		
		return ("Hellow World").getBytes();
	}
	
	
}
