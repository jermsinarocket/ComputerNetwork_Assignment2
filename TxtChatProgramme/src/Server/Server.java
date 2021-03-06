package Server;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
	
	//The Inet Address
	private final InetAddress addr;
	//The server port
	private final int serverPort;
	// The server socket.
	private static ServerSocket serverSocket;
	

	private static ArrayList<ClientThread> clientList = new ArrayList<>();
	
	public Server(int serverPort,InetAddress addr) {
		this.serverPort = serverPort;
		this.addr = addr;
	}
	
	public List<ClientThread> getClientList(){
		return clientList;
	}
	 
	@Override 
	public void run(){

		// a client socket for each separate connection is assigned to a new a client
		// thread.
		try {
		
			// Open a Server Socket on the Port Number
			serverSocket = new ServerSocket(serverPort,0,addr);

			while (true) {

				// Start accepting Connections
				System.out.println("Waiting to accept Client Connection...");
				Socket clientSocket = serverSocket.accept();
				System.out.println("Accepted connection from " + clientSocket.getInetAddress() + ", port " + clientSocket.getPort() + "\n");

				ClientThread thread = new ClientThread(this, clientSocket);
				clientList.add(thread);
				thread.start();
				

			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
