package kuxhausen.networks;

public class Packet {
	public Header h;
	public byte[] data;
	
	class Header{
		int sequenceNumber;
		byte flags;
	}
	
}
