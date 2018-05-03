import java.io.*;
import java.net.*;
import java.util.Vector;
/*
 * Contributors: Beth Cheyney, Dylan Wulfson, Eli Rangel
 * Techniques in Programming 20203
 * 
 * Program: A server to handle multiple threads providing a group chat and direct messaging
 * 			service. 
 * Essential techniques: Use of a collection of classes to define threads of a chat server. 
 * 
 * Use "@" at the beginning of a message to direct message someone. use the command "ls" to list the users on all text areas.
 */

public class ChatServer {
	//Declare the port on which I am running the server
	public final static int PORT = 5535;
	public static void main (String[] args) throws IOException {
		//Declare a Vector to store the instances of the ChatServerThread class, a boolean to listen, and a ServerSocket.
		Vector<ChatServerThread> b = new Vector<ChatServerThread>();
		boolean listen = true;
		ServerSocket serverSocket = null;
		//Try to make a ServerSocket using the port specified.
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println(PORT + " Could not be heard");
			System.exit(1);
		}
//While Listen is true try to accept any incoming connection.
		while (listen) {
			try {
				/*
				 *Set socket e = to the serverSocket as you attempt to accept connections.
				 *Accept the connection and create a BufferedReader to setUsername and 
				 *PrintWriter to pass to the class instances.
				 */
				Socket e = serverSocket.accept();
				BufferedReader bf = new BufferedReader(new InputStreamReader(e.getInputStream()));
				//Make a printwriter that automatically flushes.
				PrintWriter os = new PrintWriter(new BufferedOutputStream(e.getOutputStream()), true);
				//Create an instance of the ChatServerThread with the Socket, vector of threads, and print writer passed to it.
				ChatServerThread g = new ChatServerThread(e, b, os);
				//User the setUsername method to set userName of thread.
				g.setUsername(bf.readLine());
				System.out.println(g.getUsername());
				//Add the thread to the vector, then start it.
				b.add(g);
				g.start();
				System.out.println("Accepted connection port:  " + e.getInputStream().toString() +" Username: "+ g.getUsername());
				System.out.println("Vector: " + b);
				System.out.println("Number of clients: " + b.size());

				//Otherwise if no connections happen continue to listen
			} catch (IOException e) {
				System.out.println("Failure to confirm connection " + PORT);
				continue;
			}

//			for(int i=0; i<b.size(); i++) {
//				if (b.get(i).getSocket().getInputStream().read() == -1) {
//					System.out.println("Client: " + b.get(i) + " is dead...");
//					b.remove(i);
//				} else if(b.get(i).getSocket().getInputStream().read() >=0) {
//					System.out.println("Thread is waiting for input");
//				}
//			}
		}

	}
}
/*
 * This class is created to create instances of the clients so that you operate on them individually.
 * Each class is passed the socket which = the serverSocket, a vector of the current threads,
 * and a PrintWriter so you can print any messages.
 */
class ChatServerThread extends Thread {
	String inputLine, username;
	Socket socket = null;
	Vector<ChatServerThread> clients;
	PrintWriter pw;

	ChatServerThread(Socket socket, Vector<ChatServerThread> clients, PrintWriter os) {
		this.socket = socket;
		this.clients = clients;
		this.pw = os;
	}
	//used to get the socket to then read from it to test connection.
	public Socket getSocket() {
		return socket;
	}
	//Default run method
	public void run() {
		//indicate that the run method has initiated.
		System.out.println("Run is running.");
		/*
		 * Here we try to create a buffered reader using the socket input stream.
		 * We then say "while" there are things to read run the if statement.
		 */
		try { 
			BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println(is + " Buffered Reader Output");
			while((inputLine = is.readLine()) != null) {
				//if there is an '@' in the beginning of the message then know it is for a direct message.
				if(inputLine.charAt(0) == '@') {
					//get the name of the user which is the string immediately following '@'
					// I.e. "@Eli" the string to would = "eli".
					String to = inputLine.substring(1, inputLine.indexOf(" ")).toLowerCase();
					//Iterate through the vector and test each client for the if statement.
					for(int j=0; j<clients.size(); j++) {
						//if the username of the client it is looking at is the name we're looking for
						//print ONLY to the user specified the message attached with the rest of the input line.
						if(clients.get(j).getUsername().toLowerCase().equals(to)) {
							//String from the first space to the end of the String.
							clients.get(j).pw.println(inputLine.substring(inputLine.indexOf(" ")));				
						}
					}
					//Otherwise if there is no Direct Message indicator in the Input then print to everyone the input message.
				//} 
//				else if(inputLine.charAt(0) == 'l' && inputLine.charAt(1) == 's'){
//					for(int i=0; i<clients.size(); i++) {
//						for(int j=0; j<clients.size(); j++) {
//							clients.get(i).pw.println(clients.get(j).getUsername());
//						}
//					}
				} else {
					for(int i=0; i<clients.size(); i++) {
						//print to everyone the message recieved from a String.
						clients.get(i).pw.println(this.getUsername() + ": " + inputLine);
					}
				}
			} 
			}catch(IOException e) {
				System.out.println("I/O Error " + e);
			}
	}
	//getUsername for when you are searching for someone to Direct Message.
	public String getUsername() {
		return username;
	}
	//setUsername for when you start a thread and add it to the vector.
	public void setUsername(String username) {
		this.username = username;
	}

}
