import java.io.*;
import java.net.*;
import java.util.Vector;

public class ChatServer {
	public final static int PORT = 5517;
	public static void main (String[] args) throws IOException {
		Vector<ChatServerThread> b = new Vector<ChatServerThread>();
		boolean listen = true;
		ServerSocket serverSocket = null;
		String username;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println(PORT + " Could not be heard");
			System.exit(1);
		}
		while (listen) {
			try {
				Socket e = serverSocket.accept();
				DataInputStream dis = new DataInputStream(e.getInputStream());
				DataOutputStream dos = new DataOutputStream(e.getOutputStream());
				PrintWriter os = new PrintWriter(new BufferedOutputStream(e.getOutputStream()), true);
				username = dis.readUTF();
				System.out.println(username);
				ChatServerThread g = new ChatServerThread(e, b, username, dos, dis, os);
				b.add(g);
				g.start();
				System.out.println("" + dis);
				System.out.println("Accepted connection " + e);
				System.out.println("Vector:" + b);

			} catch (IOException e) {
				System.out.println("Failed to connect to port " + PORT);
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
	/*
	 * Do I need data input and output streams if I am trying to construct a printwriter
	 * that the update method uses to print the String history of the chat to the client UI.
	 */
	ChatServerThread(Socket socket, Vector<ChatServerThread> clients, String username, DataOutputStream dos, DataInputStream dis, PrintWriter os) {
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
				/*
				 * 
				 */
				//pw.println(inputLine);

				if(inputLine.contains("@")) {
					String to = inputLine.substring(inputLine.indexOf("@"), inputLine.indexOf(" "));
					for(int j=0; j<clients.size(); j++) {
						if(clients.get(j).getName().equals(to)) {
							clients.get(j).pw.println(inputLine.substring(inputLine.indexOf(" ")));
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
