package Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
* The chat client thread. This client thread opens the input and the output
* streams for a particular client. As long as it receive data, echos that data back to all
* other clients. When a client leaves the chat room this thread is terminated.
*/
public class ClientThread extends Thread {

	private DataInputStream inputStream = null;
	private PrintStream outputStream = null;
	private final Socket clientSocket;
	private final Server server;

	private ArrayList<String> blockList;

	private String name;

	public ClientThread(Server server,Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.server = server;

		blockList = new ArrayList<String>();
	}


	public boolean isBlocked(String name) {
		if (blockList.size() < 0)
			return false;
		else {
			return blockList.contains(name);
		}
	}

	@SuppressWarnings("deprecation")
	public void run() {

		try {
			//For Receiving Messages from Client
			inputStream = new DataInputStream(clientSocket.getInputStream());
			//For Writing Message to Client
			outputStream = new PrintStream(clientSocket.getOutputStream());
			outputStream.println("Enter your name to begin");
			// Name of User
			name = inputStream.readLine().trim();
			outputStream.println("\nHi " + name + "! Welcome to our chat room!\nType /help to see list of available commands");
			
			// Broadcast to all other users that this.client has entered the chatroom
			List<ClientThread> clientList = server.getClientList();
			for (ClientThread client : clientList) {
				if (client != this) {
					client.outputStream.println("*** A new user " + name + " has entered the chat room! ***");
				}
			}
			
			while (true) {
				String line = inputStream.readLine();
				if (line.length() <= 0)
				continue;
				//Quits the chatroom
				else if (line.startsWith("/quit")) {
					break;
				}
				
				//Blocks a user
				//Enter /block <name> to block a user.
				else if (line.startsWith("/block")) {
					String[] temp = line.split(" ");
					blockList.add(temp[1]);
					outputStream.println(temp[1] + " is blocked.");
					continue;
				}
				
				//Unblocks a user
				//Enter /unblock <name> to unblock a user
				else if (line.startsWith("/unblock")) {
					String[] temp = line.split(" ");
					blockList.remove(temp[1]);
					outputStream.println(temp[1] + " is unblocked.");
					continue;
				}
				 
				//Show who is online
				else if(line.startsWith("/online")) {
					outputStream.println("============" + "\n" +
										"ONLINE USERS" + "\n" +
										"=============");
					for (ClientThread client : clientList) {
						this.outputStream.println(client.name);
					}
					continue;
				}
				else if (line.startsWith("/help")) {
					// explanation for rules of chat room.
					outputStream.println("\n========== \n" +
										 "COMMANDS \n" +
										 "==========");
					outputStream.println("/online : Show Users that are online");
					outputStream.println("/whisper <name> : Send a private message");
					outputStream.println("/block <name> : Block a User");
					outputStream.println("/unblock <name> : Unblock a User");
					outputStream.println("/quit: Exit the Chat \n");

					continue;
				}
				
				//whisper code.
				//if you enter /name, you could whisper to the user.
				else if (line.startsWith("/")) {
					boolean found = false;
					int index = line.indexOf(" ");
					if (index != -1) {
						String whis = line.substring(1, index); //whis is client's name who get the message.
						for (ClientThread client : clientList) {
							if (client != null && client.name.equals(whis)){
								client.outputStream.println(name + " >> " + line.substring(index + 1));
								found = true;
							}
						}
		
					}
					
					if(!found) {
						outputStream.println(name + " cannot be found");
					}
					continue;
				}
				
			
				for (ClientThread client : clientList) {
					if (client != null) {
						if (blockList.contains((String) client.name))
						continue;
						//Broadcast message to all !blocked users.
						if (!client.isBlocked(name))
						client.outputStream.println("<" + name + ">" + line);
					}
				}
			}
			
			//broadcast when a user exits the chatroom
			for (ClientThread client : clientList) {
				if (client != null && client != this) {
					client.outputStream.println("\n*** The user " + name + " is leaving the chat room! ***\n");
				}
			}
			
			outputStream.println("\nYOU HAVE LEFT THE CHATROOM\n");

			/*
			* Close the output stream, close the input stream, close the
			* client socket.
			*/
			server.getClientList().remove(this);
			inputStream.close();
			outputStream.close();
			clientSocket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
