package chatserver;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	
	// Server Listening port
	private static int PORT = 12345;
	// Beginning port for games
	private static int gamePort = 14567;
	
	// Hash set used for synchronisity, User overides hashset
	private static HashSet<User> users = new HashSet<User>();
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

	public static void main(String[] args) throws IOException {
		
		System.out.println("Server Running...");
		
		// Create server socket listening on port
		ServerSocket listener = new ServerSocket(PORT);
		
		// Allows for reuse of port
		listener.setReuseAddress(true);
		
		try{
			while(true){
				// Wait for someone to connect then creates new handler thread
				// for that connection
				new Handler(listener.accept()).start();
			}
		} finally{
			// Closes the socket
			listener.close();
		}
	}
	
	
	/*
	 * Each unique connection gets a new thread
	 */
	public static class Handler extends Thread{
		
		// User connected to this socket
		private User user;
		
		// Users socket
		private Socket socket;
		
		// Used to read from user
		private BufferedReader in;
		
		// Used to send to user
		private PrintWriter out;
		
		public Handler(Socket socket){
			this.socket = socket;
		}
		
		public void run(){
			System.out.println("Handler Started...");
			//System.out.println(socket.toString());
			
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(),true);
				
				
				while(true){
					// Asks for name from client
					out.println("SUBMITNAME");
					
					// Recieves name from client
					String name = in.readLine();
					
					
					System.out.print(name);
					System.out.print("  "+socket.getInetAddress().getHostAddress());
					System.out.println(":"+socket.getPort());
					
					// Create new user from info gathered
					user = new User(name,socket.getInetAddress().getHostAddress(),socket.getPort(),out);
					
					if(name == null)
						return;
					
					// Synchronizes the users then sends all current users
					// to newly connected client
					// then adds new user
					synchronized(users){
						if(!users.contains(user)){
							for(User u : users){
								out.println("USER " + u.getName());
							}
							users.add(user);
							break;
						}
					}
				}
				
				// Sends new user to all other clients connected
				for(PrintWriter writer : writers){
					writer.println("USER " + user.getName());
				}
				
				// Adds new writer to hash set
				out.println("NAMEACCEPTED");
				writers.add(out);
				
				while(true){
					String input = in.readLine();
					if(input == null)
						return;
					
					// Chat Message
					if(input.startsWith("MESSAGE")){
						for(PrintWriter writer : writers){
							//writer.println("MESSAGE " + user.getName() + ": " + input);
							writer.println(input);
						}
					}
					
					// Game Challange Invite
					if(input.startsWith("INVITE")){
						String[] invite = input.split(" ");

						System.out.println(user.getName() + " INVITES " + invite[1]);

						//find user
						String invite_name = invite[1];
						int curPort = user.getPort();

						for(User u : users){
							if(u.getName().equals(invite_name) && u.getPort() != curPort){
								u.getWriter().println("INVITE "+user.getName()+ " " + user.getAddress());
							}
						}
					}
					
					// Two Players Have Accepted
					if(input.startsWith("ACCEPT")){
						String[] accepts = input.split(" ");
						System.out.println(accepts[1] + " vs " + user.getAddress());
						System.out.println(""+gamePort + " and " + (gamePort+1));
						System.out.println("GAME SHOUDL START NOW");
						new GameServer(accepts[1],user.getAddress(),gamePort,gamePort+1,"name1","name2").start();
						gamePort += 2;
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				
				// tell all clients user is leaving
				for(PrintWriter write : writers)
					write.println("LEAVES " + user.getName());
				
				// Removes this user from hash set
				if(user != null)
					users.remove(user);
				
				// removes this writer from hashset of writers
				if(out != null)
					writers.remove(out);
				
				
				try{
					// Closes socket
					socket.close();
					System.out.println("Someone Exited");
				}catch(IOException e){
					
				}
			}
		}
	}

}
