package kuxhausen.networks;

import java.nio.ByteBuffer;

public class Packet {
	public byte[] data;
	public int sequenceNumber;
	public boolean isAck;
	public boolean isLast;
	
	public Packet(byte[] data, int sequenceNumber, boolean isLast, boolean isAck){
		this.data = data;
		this.sequenceNumber = sequenceNumber;
		this.isLast = isLast;
		this.isAck = isAck;
	}
	
	public byte[] toBytes(){
		byte[] result = new byte[data.length+5];
		ByteBuffer buf = ByteBuffer.wrap(result);
		{
			byte flags=0;
			if(isAck)
				flags+=1;
			if(isLast)
				flags+=2;
			buf.put(flags);
		}
		buf.putInt(sequenceNumber);
		buf.put(data);
		
		return result;
	}
	
	public static Packet decodePacket(byte[] encoded, int length){
		byte[] data = new byte[length-5];
		
		ByteBuffer b = ByteBuffer.wrap(encoded);
		byte flags = b.get();
		boolean	isAck = ((((int)flags)&1)==1);
		boolean	isLast = ((((int)flags)&2)==2);		
		int sequenceNumber = b.getInt();
		b.get(data);
		return new Packet(data, sequenceNumber, isLast, isAck);
	}
	
	public static class SenderPacket extends Packet{
		/** based on system.currentTimeMillis() **/
		public long timeSent;
		public boolean Acked;
		
		public SenderPacket(byte[] data, int sequenceNumber, boolean isLast, boolean isAck) {
			super(data, sequenceNumber, isLast, isAck);
		}
		
	}
}
