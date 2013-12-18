

import java.nio.ByteBuffer;

public class Packet {
	public byte[] data;
	public int sequenceNumber;
	/** in miliseconds **/
	public long timeout;
	public boolean isAck;
	public boolean isLast;
	public boolean isFinished;
	
	public static final int PACKET_HEADER_LENGTH = 9;
	
	public Packet(byte[] data, int sequenceNumber, long timeOut, boolean isFinished, boolean isLast, boolean isAck){
		this.data = data;
		this.sequenceNumber = sequenceNumber;
		this.timeout = timeOut;
		this.isLast = isLast;
		this.isAck = isAck;
		this.isFinished = isFinished;
	}
	
	public byte[] toBytes(){
		byte[] result = new byte[data.length+PACKET_HEADER_LENGTH];
		ByteBuffer buf = ByteBuffer.wrap(result);
		{
			byte flags=0;
			if(isAck)
				flags+=1;
			if(isLast)
				flags+=2;
			if(isFinished)
				flags+=4;
			buf.put(flags);
		}
		buf.putInt(sequenceNumber);
		//only considering the last 32 bits of timeout merely constrains us to a max timeOut of 24.8 days!
		buf.putInt((int)timeout);
		buf.put(data);
		
		return result;
	}
	
	public static Packet decodePacket(byte[] encoded, int length){
		byte[] data = new byte[length-PACKET_HEADER_LENGTH];
		
		ByteBuffer b = ByteBuffer.wrap(encoded);
		byte flags = b.get();
		boolean	isAck = ((((int)flags)&1)==1);
		boolean	isLast = ((((int)flags)&2)==2);
		boolean	isFinished = ((((int)flags)&4)==4);
		int sequenceNumber = b.getInt();
		//remember timeout went over the wire as an int
		long timeout = b.getInt();
		b.get(data);
		return new Packet(data, sequenceNumber, timeout, isFinished, isLast, isAck);
	}
	
}
