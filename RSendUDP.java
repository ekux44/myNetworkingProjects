import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import edu.utulsa.unet.UDPSocket; //import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;


public class RSendUDP extends RUDP implements edu.utulsa.unet.RSendUDPI, Runnable{

	public static void main(String[] args)
	{
		RSendUDP sender = new RSendUDP();
		sender.setMode(1);
		sender.setModeParameter(512);
		sender.setTimeout(1000);
		sender.setFilename("important.txt");
		sender.setLocalPort(23456);
		sender.setReceiver(new InetSocketAddress("localhost",32456));
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
	UDPSocket socket;
	
	@Override
	public boolean sendFile() {
		try {
			socket = new UDPSocket(getLocalPort());
			mtu = socket.getSendBufferSize();
			byte[] message = getMessage();
			data = getSegmentedMessage(message);
			lAckedSequence = -1;
			lSent = -1;
			System.out.println("Sending "+this.filename+" from "+socket.getLocalAddress().getHostAddress()+":"+this.getLocalPort()+" to "+reciever.toString()+" with "+message.length+" bytes");
			System.out.println("Using "+mode.toString());
			Long startTime = System.currentTimeMillis();
			
			new Thread(this).start();
			while(lAckedSequence<(data.length-1)){
				if((lSent-lAckedSequence)<slidingWindowSize && ((lSent+1)<data.length)){
					SenderPacket p = data[(lSent+1)];
					socket.send(new DatagramPacket(p.toBytes(), p.toBytes().length, reciever.getAddress(), reciever.getPort()));
					p.timeSent = System.currentTimeMillis();
					lSent++;
					
					System.out.println("Message "+lSent+" sent with "+p.data.length+" byes of actual data");
					
				} else if(windowGetFirstUnAckedTimeout()!=null){
					int oldest = windowGetFirstUnAckedTimeout();
					System.out.println("Message "+oldest+" timed-out");
					SenderPacket p = data[oldest];
					socket.send(new DatagramPacket(p.toBytes(), p.toBytes().length, reciever.getAddress(), reciever.getPort()));
					System.out.println("Message "+oldest+" sent with "+p.data.length+" byes of actual data");
				} 
				
			}
			
			Long stopTime = System.currentTimeMillis();
			System.out.println("Successfully transferred "+this.filename+" ("+message.length+" bytes) in "+((stopTime-startTime)/1000.0)+"seconds");
		}
			catch(Exception e){ e.printStackTrace();
		}

		return false;
	}
	
	private synchronized void checkUpdatelAckedSequence(){
		for(int i = Math.max(0, lAckedSequence); i<= lSent; i++){
			if(data[i].Acked)
				lAckedSequence = i;
			else
				return;
		}
	}
	
	private synchronized Integer windowGetFirstUnAckedTimeout(){
		for(int i = Math.max(0, lAckedSequence); i<=lSent; i++){
			if(data[i].Acked==false && ((System.currentTimeMillis()-data[i].timeSent)>this.timeOut))
				return i;
		}
		return null;
	}
	
	private synchronized byte[] getMessage(){
		return ("How now brown cow. How now brown cow. How now brown cow.").getBytes();
	}
	private synchronized SenderPacket[] getSegmentedMessage(byte[] message){
		int mdu = mtu-5;
		int numSegments = (int)Math.ceil(message.length/((double)mdu));
		SenderPacket[] output = new SenderPacket[numSegments];
		for(int i = 0; i< numSegments; i++){
			System.out.println("getSegmentMessage num "+i+" out of :"+numSegments);
			byte[] data = Arrays.copyOfRange(message, i*mdu, Math.min((i+1)*mdu, message.length));
			output[i] = new SenderPacket(data, i, (i==numSegments-1), false);
		}
		return output;
	}

	@Override
	public void run() {
		while(lAckedSequence<(data.length-1)){
			byte [] buffer = new byte[mtu];
			DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
			System.out.println("trying to recieve ack");
			try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Packet p = Packet.decodePacket(buffer, packet.getLength());
			if(p.isAck){
				data[p.sequenceNumber].Acked = true;
				checkUpdatelAckedSequence();
				
				System.out.println("Message "+p.sequenceNumber+" acknowledged");
			}
		}
	}
	
	public class SenderPacket extends Packet{
		/** based on system.currentTimeMillis() **/
		public long timeSent;
		public boolean Acked;
		
		public SenderPacket(byte[] data, int sequenceNumber, boolean isLast, boolean isAck) {
			super(data, sequenceNumber, isLast, isAck);
		}
		
	}
}
