import static java.lang.System.*;

public class Talk {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length<1){
			out.println("Talk must be started with a flag. See Talk -help");
		} else parse:{
			Mode tMode = null;
			String hostnameOrIP = null;
			Integer portnumber = null;
			int index = 0;
			
			switch (args[index]){
				case "-h": 
					tMode = Mode.Client;
					break;
				case "-s": 
					tMode = Mode.Server;
					break;
				case "-a": 
					tMode = Mode.Auto;
					break;
				case "-help":
					out.println("\nTalk created by Eric Kuxhausen" +
							"\nTalk is a simple bidirectional networked chat program." +
							"\n" +
							"\nTalk -h [hostname|IPaddress] [-p portnumber] \tto start in client mode" +
							"\nTalk -s [-p portnumber]                      \tto start in server mode" +
							"\nTalk -a [hostname|IPaddress] [-p portnumber] \tto start in automode" +
							"\nTalk -help                                   \tto show this page");
					break parse;
				default : 
					out.println("Invalid flag. See Talk -help");
					break parse;
			}
			if(index+1<args.length){
				if(args[index+1].equals("-p")){
					index+=2;
					if(index<args.length){
						try {
							portnumber = Integer.parseInt(args[index]);
						} catch (NumberFormatException e){
							out.println("Invalid portnumber. See Talk -help");
							break parse;
						}
					}
					else{
						out.println("Missing portnumber. See Talk -help");
						break parse;
					}
					
				} else if (tMode == Mode.Client || tMode == Mode.Auto) {
					index++;
					hostnameOrIP = args[1];
					if(index+1<args.length && args[index+1].equals("-p")){
						index+=2;
						if(index<args.length){
							try {
								portnumber = Integer.parseInt(args[index]);
							} catch (NumberFormatException e){
								out.println("Invalid portnumber. See Talk -help");
								break parse;
							}
						}
						else{
							out.println("Missing portnumber. See Talk -help");
							break parse;
						}	
					}
				} 
			}
			if(index+1<args.length){
				out.println("Invalid input. See Talk -help");
			}
			else{
				Talk t = new Talk(tMode, hostnameOrIP, portnumber);
			}
		}
		
	}
	
	enum Mode{
		Client, Server, Auto
	}
	
	public Talk(Mode t, String hostnameOrIP, Integer portNumber){
		if(portNumber==null)
			portNumber = 12987;
		
		
		out.println(t.toString());
		if(hostnameOrIP!=null)
			out.println(hostnameOrIP);
		out.println(portNumber);
	}
}
