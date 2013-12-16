import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

import static java.lang.System.*;


public class HttpServer implements Runnable{

	
	public static void main(String[] args) {	
		if(args.length!=1){
			out.println("HttpServer must be started with only the portNumber argument.");
		}
		try{
			int portNumber = Integer.parseInt(args[0]);
			ServerSocket server = new ServerSocket(portNumber);
			while (true) {
				Socket sock = server.accept();
				System.out.println("Connected");
				new Thread(new HttpServer(sock)).start();
			}
		} catch (Exception e){
			out.println("HttpServer must be started with a valid, unused portNumber");
		}
	}

		Socket socket;

		HttpServer(Socket csocket) {
			this.socket = csocket;
		}
	
	@Override
	public void run() {
		try {
            InputStream input  = socket.getInputStream();
            String method = "";
            String requestURI = null;
            String host = null;
            int statusCode = -1;
            byte[] body = null;
            
            try{
            	
            	Scanner in = new Scanner(input);
	            
	            method = in.next();
	            requestURI = in.next();
	            
	            String hostLine = in.nextLine();
	            while(!hostLine.substring(0,5).equals("Host:") && in.hasNextLine()){
	            	hostLine = in.nextLine();
	            }
	            Scanner hostScan = new Scanner(hostLine);
	            if(hostScan.next().equals("Host:")){
	            	host = hostScan.next();
	            }else{
	            	statusCode = 400;
	            }
	            hostScan.close();
            } catch (Error e){
            	statusCode = 400;
            }
            OutputStream output = socket.getOutputStream();
            
            if(statusCode!=400){
            	switch(method){
	            	case "GET:":
	            		body = getResource(requestURI);
	            		if(body==null)
	            			statusCode= 404;
	            		else{
	            			statusCode = 200;
	            		}
	            		//TODO
	            		break;
	            	case "HEAD:":
	            		body = getResource(requestURI);
	            		if(body==null)
	            			statusCode= 404;
	            		else{
	            			statusCode = 200;
	            		}
	            		//TODO
	            		break;
	            	case "OPTIONS:":
	            		statusCode = 501;
	            		break;
	            	case "POST:":
	            		statusCode = 501;
	            		break;
	            	case "PUT:":
	            		statusCode = 501;
	            		break;
	            	case "DELETE:":
	            		statusCode = 501;
	            		break;
	            	case "TRACE:":
	            		statusCode = 501;
	            		break;
	            	case "CONNECT:":
	            		statusCode = 501;
	            		break;
	            	default:
	            		statusCode = 400;
            	}
            
            }
            
            byte[] message = null;
            byte[] header =("HTTP/1.1 "+statusCode+" "+getReasonPhrase(statusCode)+"\n"+
            		"Server: cs4333httpserver/1.0.0\n"+
            		"Content-Length: "+getLengthOfResource(body)+"\n"+
            		"Content-Type: "+getTypeOfResource(requestURI)+"\n"+
            		"\n").getBytes();
            if(statusCode==200 && method.equals("GET:")){
            	message = new byte[header.length+body.length];
            	ByteBuffer buf = ByteBuffer.wrap(message);
            	buf.put(header);
            	buf.put(body);
            }else{
            	message = header;
            }
            output.write(message);
            output.close();
            input.close();
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
	}
	
	public byte[] getResource(String requestURI){
		try {
			if(requestURI.substring(1).contains("/"))
				return null; //file is in a subdirectory (not allowed, so 404)
			
			FileInputStream fb = new FileInputStream ("/public_html"+requestURI);
			byte[] result = new byte[fb.available()];
			fb.read(result);
			return result;
			
		} catch (IOException e) {
			return null;
		}
	}
	
	public static String getTypeOfResource(String requestURI){
		if(requestURI.matches(".*\\.html")||requestURI.matches(".*\\.htm"))
			return "text/html";
		if(requestURI.matches(".*\\.gif"))
			return "image/gif";
		if(requestURI.matches(".*\\.jpeg")||requestURI.matches(".*\\.jpg"))
			return "image/jpeg";
		if(requestURI.matches(".*\\.pdf"))
			return "application/pdf";
		return "text";
	}
	
	public static int getLengthOfResource(byte[] body){
		if(body==null)
			return 0;
		else
			return body.length;
	}
	
	public static String getReasonPhrase(int statusCode){
		switch(statusCode){
			case 200: return "OK";
			case 400: return "Bad Request";
			case 404: return "Not Found";
			case 501: return "Not Implemented";
		}
		return null;
	}
}
