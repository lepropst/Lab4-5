import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ChatServer {
	public final static int PORT = 5562;
	static RandomAccessFile history;
	public static void main (String[] args) throws IOException {
		ArrayList<Object> clients = new ArrayList<Object>();
		boolean listen = true;
		ServerSocket serverSocket = null;
		String history = null;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println(PORT + " Could not be heard");
			System.exit(1);
		}
		while (listen) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
				System.out.println("Accepted connection");
			} catch (IOException e) {
				System.out.println("Failed to connect to port " + PORT);
				continue;
			}
			
			ChatServerThread e;
			e = new ChatServerThread(clientSocket, history, clients);
			e.start();
			clients.add(e);
			System.out.println("Thread Started");
			System.out.println(e);
		}
		try {
			serverSocket.accept();
			System.out.println("Socket closed");
		} catch(IOException e) {
			System.out.println("IO on closing RAF");
		}
	}
}
	class ChatServerThread extends Thread {
		String inputLine, outputLine;
		Socket socket = null;
		ArrayList clients;
		private String history;
		
		
		ChatServerThread(Socket socket, String history, ArrayList clients) {
			this.socket = socket;
			this.history = history;
			this.clients = clients;
		}

		public String write() {
			outputLine = inputLine;
			outputLine = inputLine;
			return history = history + "\n" + outputLine;
		}
	
		public void run() {
			
			try { 
				BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter os = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
				
				while((inputLine = is.readLine()) != null) {
					
					for (int i=0; i<clients.size()-1; i++) {
						clients.get(i);
						os.println(history);
					os.flush();
					
				}
			}
				os.close();
				is.close();
				socket.close();
			} catch(IOException e) {
				System.out.println("I/O Error" + e);
			}
		}
		}
	
