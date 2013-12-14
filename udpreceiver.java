import java.net.DatagramPacket;
import edu.utulsa.unet.UDPSocket; //import java.net.DatagramSocket;
import java.net.InetAddress;

public class udpreceiver {
	static final int PORT = 32456;
	public static void main(String[] args)
	{
		try
		{
			byte [] buffer = new byte[11];
			UDPSocket socket = new UDPSocket(PORT);
			DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
			socket.receive(packet);
			InetAddress client = packet.getAddress();
			System.out.println(" Received'"+new String(buffer)+"' from " +packet.getAddress().getHostAddress());
		}
		catch(Exception e){ e.printStackTrace(); }
	}
}
