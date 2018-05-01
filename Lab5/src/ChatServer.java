import java.io.*;
import java.net.*;
import java.util.Vector;

public class ChatServer {
	public final static int PORT = 5524;
	public static void main (String[] args) throws IOException {
		Vector<ChatServerThread> b = new Vector<ChatServerThread>();
		boolean listen = true;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println(PORT + " Could not be heard");
			System.exit(1);
		}
		while (listen) {
			try {
				Socket e = serverSocket.accept();
				
//				DataInputStream dis = new DataInputStream(e.getInputStream());
//				DataOutputStream dos = new DataOutputStream(e.getOutputStream());
				PrintWriter os = new PrintWriter(new BufferedOutputStream(e.getOutputStream()), true);
				BufferedReader bf = new BufferedReader(new InputStreamReader(e.getInputStream()));
				ChatServerThread g = new ChatServerThread(e, b, os);
				g.setUsername(bf.readLine());
				System.out.println(g.getUsername());
				b.add(g);
				g.start();
				System.out.println("Accepted connection port:  " + e +" Username: "+ g.getUsername());
				System.out.println("Vector: " + b);
				System.out.println("Number of clients: " + b.size());

			} catch (IOException e) {
				System.out.println("Failure to confirm connection " + PORT);
				continue;
			}




			System.out.println("Thread Started");

		}
		System.out.println(b);
		try {
			serverSocket.accept();
			System.out.println("Socket closed");
		} catch(IOException e) {
			System.out.println("IO on closing RAF");
		}
		
			
		}
	}
class ChatServerThread extends Thread {
	String inputLine, outputLine, username;
	Socket socket = null;
	Vector<ChatServerThread> clients;
	DataOutputStream dos;
	DataInputStream dis;
	PrintWriter pw;
	
	ChatServerThread(Socket socket, Vector<ChatServerThread> clients, PrintWriter os) {
		this.socket = socket;
		this.clients = clients;
		this.dos = dos;
		this.dis = dis;
		this.username = username;
		this.pw = os;
	}
	public void run() {
		//write();
		System.out.println("Run is running.");
		try { 
			BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));


			while((inputLine = is.readLine()) != null) {
				if(inputLine.contains("@")) {
					String to = inputLine.substring(inputLine.indexOf("@"), inputLine.indexOf(" "));
					for(int j=0; j<clients.size(); j++) {
						if(clients.get(j).getName().equals(to)) {
							clients.get(j).pw.println(inputLine.substring(inputLine.indexOf(" ")));
							System.out.println(inputLine);
						}
					}
				} else {
					for(int i=0; i<clients.size(); i++) {
						
						clients.get(i).pw.println(inputLine);
					}
				}
				//socket.close();
			} }catch(IOException e) {
				System.out.println("I/O Error " + e);
			}
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
