package chatserver;

import java.io.PrintWriter;

public class User {

	// User name
	private String name;
	
	// User ip address
	private String address;
	
	// Port used for connection
	private int port;
	
	// Writer used to send user info
	private PrintWriter out;
	
	public User(String name,String address,int port,PrintWriter out){
		this.name = name;
		this.address = address;
		this.port = port;
		this.out = out;
	}
	
	@Override
	public boolean equals(Object o){
		System.out.println("Used Equals");
		return false;
	}
	
	@Override
	/* Creates a unique hashcode for each new user that connects
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		int hash = 0;
		String[] adds = address.split(".");
		for(int i = 0;i < adds.length;i++){
			hash += Integer.parseInt(adds[i]);
		}
		hash += port;
		return hash;
	}
	
	public String getName(){
		return name;
	}
	
	public String getAddress(){
		return address;
	}
	
	public int getPort(){
		return port;
	}
	
	public PrintWriter getWriter(){
		return out;
	}
}
