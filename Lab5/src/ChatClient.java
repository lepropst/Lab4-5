import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JApplet;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

public class ChatClient implements ActionListener {
	private Socket socket = null;
	private DataInputStream  console = null;
	private DataOutputStream streamOut = null;

	public ChatClient(String serverName, int serverPort){
		System.out.println("Establishing connection. Please wait ...");
		try{
			socket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + socket);
			start();
		} catch(UnknownHostException uhe) {
			System.out.println("Host unknown: " + uhe.getMessage());
		} catch(IOException ioe) {
			System.out.println("Unexpected exception: " + ioe.getMessage());
		}
		String line = "";
		while (!line.equals(".bye")) {
			try {
				line = console.readLine();
				streamOut.writeUTF(line);
				streamOut.flush();
			} catch(IOException ioe) {
				System.out.println("Sending error: " + ioe.getMessage());
			}
	}
	}
	public void start() throws IOException {
	console   = new DataInputStream(System.in);
	streamOut = new DataOutputStream(socket.getOutputStream());
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
	public static void main(String args[]) {
		ChatClient client = null;
		if (args.length != 2)
			System.out.println("Usage: java ChatClient host port");
		else
			client = new ChatClient(args[0], Integer.parseInt(args[1]));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}


class ChatUI extends JApplet {
	private JTextField message;
	private JTextField server;
	private JTextField port;

	public ChatUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel controls = new JPanel();
		getContentPane().add(controls, BorderLayout.SOUTH);
		controls.setLayout(new GridLayout(2, 1, 0, 0));
		
		JPanel chatstuff = new JPanel();
		controls.add(chatstuff);
		chatstuff.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		message = new JTextField();
		chatstuff.add(message);
		message.setColumns(35);
		
		JButton send = new JButton("Send");
		chatstuff.add(send);
		
		JPanel conectionstuff = new JPanel();
		controls.add(conectionstuff);
		conectionstuff.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblServer = new JLabel("Server IP:");
		conectionstuff.add(lblServer);
		
		server = new JTextField();
		conectionstuff.add(server);
		server.setColumns(15);
		
		JLabel lblPort = new JLabel("Port:");
		conectionstuff.add(lblPort);
		
		port = new JTextField();
		conectionstuff.add(port);
		port.setColumns(10);
		
		JButton btnConnect = new JButton("Connect");
		conectionstuff.add(btnConnect);
		
		JPanel chatlog = new JPanel();
		getContentPane().add(chatlog, BorderLayout.CENTER);
		
		JTextArea log = new JTextArea();
		log.setRows(12);
		log.setColumns(35);
		chatlog.add(log);

	}

}
