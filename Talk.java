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
		
//		out.println(t.toString());
//		if(hostnameOrIP!=null)
//			out.println(hostnameOrIP);
//		out.println(portNumber);
		
		switch (t){
			case Client: clientMode(hostnameOrIP,portNumber);
			case Server: serverMode(portNumber);
		}
		
	}
	
	public boolean clientMode(String serverName, Integer portNumber){
		String message = null;
		try{
			Socket socket = new Socket(serverName, portNumber);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			while(true){
				message = reader.readLine();
				writer.println(message);
			}
		} catch(UnknownHostException e){
			out.println("Unknown host:"+serverName);
			exit(1);
		} catch(IOException e){
			out.println("No I/O");
			exit(1);
		}
		
		return true;
	}
	
	public boolean serverMode(Integer serverPortNumber){
		BufferedReader reader = null;
		String message = null;
		Socket client = null;
		ServerSocket server = null;
		try {
			server = new ServerSocket(serverPortNumber);
			out.println("Server listening on port "+serverPortNumber);
		} catch (IOException e) {
			out.println("Accept failed on port "+serverPortNumber);
			exit(-1);
		}
		try {
			client = server.accept();
			out.println("Server accepted connection from "+client.getInetAddress());
		} catch(IOException e){
			out.println("Accept failed on port "+serverPortNumber);
			exit(-1);
		}
		try{
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch(IOException e){
			out.println("Couldn't get an inputStream for the Client");
			exit(-1);
		}
		try{
			while(true){
				if(reader.ready()){
					message = reader.readLine();
					out.println(message);
				}
			}
		} catch(IOException e){
			out.println("Read failed");
			exit(-1);
		}
		return true;
	}
	
}









