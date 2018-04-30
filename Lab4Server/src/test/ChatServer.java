package test;
/*
 * Decompiled with CFR 0_123.
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Vector;

public class ChatServer {
	public final static int PORT = 5555;
	static RandomAccessFile history;
	public static void main (String[] args) throws IOException {
		Vector<Object> b = new Vector<Object>();
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
			clientSocket = serverSocket.accept();
			try {
				Object e = serverSocket.accept();
				DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
				DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
				e = new ChatServerThread(clientSocket, history, b, dos, dis);
				b.add(e);
				((Thread) e).start();
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
	String inputLine, outputLine;
	Socket socket = null;
	Vector<Object> clients;
	private String history;
	DataOutputStream dos;
	DataInputStream dis;

	ChatServerThread(Socket socket, String history, Vector<Object> clients, DataOutputStream dos, DataInputStream dis) {
		this.socket = socket;
		this.history = history;
		this.clients = clients;
		this.dos = dos;
		this.dis = dis;
	}

	public synchronized String write() {
		try {
			PrintWriter os = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
			outputLine = dis.readUTF();
			System.out.println(outputLine);
			os.println(outputLine);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return history = history + "\n" + outputLine;
		}
		

	public void run() {
		write();
		System.out.println("Run is running.");
		try { 
			BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter os = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));

			while((inputLine = is.readLine()) != null) {
				System.out.println(write()+ " system out print");
				
				update(os);
				os.print(inputLine);
				is.close();
				os.close();

			}
			socket.close();
		} catch(IOException e) {
			System.out.println("I/O Error" + e);
		}
	}

	private synchronized void update(PrintWriter os) {
		for(int i=0; i<clients.size()-1; i++) {
			Object ss = os;
			clients.get(i);
			((PrintStream) ss).println(history);

		}

	}
}

