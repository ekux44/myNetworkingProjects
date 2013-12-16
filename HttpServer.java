import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
            OutputStream output = socket.getOutputStream();
            long time = System.currentTimeMillis();
            output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
                    "WTF" + " - " +
                    time +
                    "").getBytes());
            output.close();
            input.close();
            System.out.println("Request processed: " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
	}
}
