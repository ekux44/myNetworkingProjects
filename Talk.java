import static java.lang.System.*;
import java.io.*;
import java.net.*;

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
				//new Talk(tMode, hostnameOrIP, portnumber);
				if(portnumber==null)
					portnumber = 12987;
				
				switch (tMode){
					case Client: 
						if(!talk(Mode.Client, portnumber, hostnameOrIP))
							out.println("Client unable to communicate with server");
						break;
					case Server: 
						if(!talk(Mode.Server, portnumber, hostnameOrIP))
							out.println("Server unable to listen on the specified port");
						break;
					case Auto: 
						if(!talk(Mode.Client, portnumber, hostnameOrIP))
							if(!talk(Mode.Server, portnumber, hostnameOrIP))
								out.println("Server unable to listen on the specified port");
				}
			}
		}
	}
	
	enum Mode{
		Client, Server, Auto
	}
	
	public static boolean talk(Mode mode, Integer serverPortNumber, String serverName){
		String message = null;
		try {
			Socket socket;
			if(mode==Mode.Server){
				ServerSocket server = new ServerSocket(serverPortNumber);
				socket = server.accept();
			}else if (mode==Mode.Client){
				socket = new Socket(serverName, serverPortNumber);
			}else{
				return false;
			}
			BufferedReader netReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedReader kbReader = new BufferedReader(new InputStreamReader(in));
			PrintWriter netWriter = new PrintWriter(socket.getOutputStream(), true);
			while(true){
				if(netReader.ready()){
					message = netReader.readLine();
					out.println("[remote]"+message);
				} else if (kbReader.ready()){
					message = kbReader.readLine();
					if(message.equals("STATUS")){						
						out.println("local address:port\t"+socket.getLocalSocketAddress());
						out.println("remote address:port\t"+ socket.getRemoteSocketAddress());
					} else
						netWriter.println(message);
				}
			}
		} catch(UnknownHostException e){
			return false;
		} catch(IOException e){
			return false;
		}
	}
}
