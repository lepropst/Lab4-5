import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;


public class ChatClient extends ChatUI implements ActionListener {
	private Socket socket = null;
	private DataInputStream  console = null;
	private DataOutputStream streamOut = null;

	
	public void chat(String serverName, int serverPort) throws IOException{
		System.out.println("Establishing connection. Please wait ...");
		try{
			socket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + socket);
			PrintWriter send = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
			start();
		} catch(UnknownHostException uhe) {
			System.out.println("Host unknown: " + uhe.getMessage());
		} catch(IOException ioe) {
			System.out.println("Unexpected exception: " + ioe.getMessage());
		}
		BufferedReader line = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		while (!line.readLine().equals(".bye")) {
			try {
				log.append(line.readLine());
			} catch(IOException ioe) {
				System.out.println("Sending error: " + ioe.getMessage());
			}
	}
	}
	public void start() {
	console   = new DataInputStream(System.in);
	try {
		streamOut = new DataOutputStream(socket.getOutputStream());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	public void stop() {
		try {
			if (console   != null)  console.close();
			if (streamOut != null)  streamOut.close();
			if (socket    != null)  socket.close();
		}
		catch(IOException ioe) {
			System.out.println("Error closing ...");
		}
	}
	public void init() {
		super.init();
		addListeners();
		//chat(server.getText(), Integer.parseInt(port.getText()));
	}
	
	public void addListeners() {
		send.addActionListener(this);
		btnConnect.addActionListener(this);
	}
	public static void main(String args[]) {
		
	//	ChatClient client = null;
	//	if (args.length != 2)
	//		System.out.println("Usage: java ChatClient host port");
	//	else
	//		client = new ChatClient(args[0], Integer.parseInt(args[1]));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnConnect) {
		//	ChatClient chat = new ChatClient(server.getText(), Integer.parseInt(port.getText()));
			//chat.start();
		}else if(e.getSource() == send) {
			//writeToServer();
		}
		
	}
	
}
