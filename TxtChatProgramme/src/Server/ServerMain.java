package Server;

import java.io.IOException;
import java.net.InetAddress;

public class ServerMain {

	public static void main(String args[]) throws IOException {
		
		//Set Port Number, can be changed
		final int portNumber = 2222;
		
		//IP-to-Host Name Resolution
		//Can be configured to connect to external IP address
		final InetAddress addr = InetAddress.getByName("127.0.0.1");
		
		System.out.println("======================================" + "\n" + "HOSTED ON " + addr +
					+ portNumber + "\n" + "======================================");
		
		Server server = new Server(portNumber,addr);
		
		//Start Server Thread
		server.start();


	}

}
 