import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import edu.utulsa.unet.UDPSocket; //import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import kuxhausen.networks.Packet;
import kuxhausen.networks.Packet.SenderPacket;

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
	
	int mtu;
	SenderPacket[] data;
	int lAckedSequence;
	int lSent;
	
	@Override
	public boolean sendFile() {
		try {
			UDPSocket socket = new UDPSocket(getLocalPort());
			mtu = socket.getSendBufferSize();
			byte[] message = getMessage();
			data = getSegmentedMessage(message, mtu);
			lAckedSequence =0;
			lSent = 0;
			while(lAckedSequence<data.length){
				if(windowGetFirstUnAckedTimeout(data)!=null){
					int oldest = windowGetFirstUnAckedTimeout(data);
					SenderPacket p = data[oldest];
					socket.send(new DatagramPacket(p.toBytes(), p.toBytes().length, reciever.getAddress(), reciever.getPort()));
				} else if((lSent-lAckedSequence)<slidingWindowSize){
					SenderPacket p = data[(lSent+1)];
					socket.send(new DatagramPacket(p.toBytes(), p.toBytes().length, reciever.getAddress(), reciever.getPort()));
					p.timeSent = System.currentTimeMillis();
					lSent++;
				} else {
					byte [] buffer = new byte[mtu];
					DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
					socket.receive(packet);
					Packet p = Packet.decodePacket(buffer, packet.getLength());
					if(p.isAck){
						data[p.sequenceNumber].Acked = true;
						checkUpdatelAckedSequence();
					}
				}
				
			}
			
			byte [] buffer = ("Hello World").getBytes();
			socket.send(new DatagramPacket(buffer, buffer.length,
 				InetAddress.getByName(SERVER), PORT));
		}
			catch(Exception e){ e.printStackTrace();
		}

		return false;
	}
	
	private void checkUpdatelAckedSequence(){
		for(int i = lAckedSequence; i<= lSent; i++){
			if(data[i].Acked)
				lAckedSequence = i;
			else
				return;
		}
	}
	
	private Integer windowGetFirstUnAckedTimeout(SenderPacket[] packets){
		for(int i = lAckedSequence; i<=lSent; i++){
			if(packets[i].Acked==false && ((System.currentTimeMillis()-packets[i].timeSent)>this.timeOut))
				return i;
		}
		return null;
	}
	
	private byte[] getMessage(){
		return ("Hellow World").getBytes();
	}
	private SenderPacket[] getSegmentedMessage(byte[] message, int mtu){
		int numSegments = (int)Math.ceil(mtu/(double)message.length);
		SenderPacket[] output = new SenderPacket[numSegments];
		for(int i = 0; i< numSegments; i++){
			byte[] data = Arrays.copyOfRange(message, i*mtu, Math.min((i+1)*mtu, message.length));
			output[i] = new SenderPacket(data, i, (i==numSegments-1), false);
		}
		return output;
	}
	
	
}
