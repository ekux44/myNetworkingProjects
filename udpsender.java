import java.net.DatagramPacket;
import edu.utulsa.unet.UDPSocket; //import java.net.DatagramSocket;
import java.net.InetAddress;

public class udpsender {
	static final String SERVER = "localhost";
	static final int PORT = 32456;

	public static void main(String[] args)
	{
		try {

			byte [] buffer = ("Hello World").getBytes();
			UDPSocket socket = new UDPSocket();
			socket.send(new DatagramPacket(buffer, buffer.length,
 				InetAddress.getByName(SERVER), PORT));
		}
		catch(Exception e){ e.printStackTrace(); }
	}
}
