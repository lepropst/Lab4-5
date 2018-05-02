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


class ChatUI extends JApplet {
	public JTextField message;
	public JTextField server;
	public JTextField port;
	JPanel chatstuff = new JPanel();
	JPanel controls = new JPanel();
	JButton send = new JButton("Send");
	JPanel conectionstuff = new JPanel();
	JLabel lblServer = new JLabel("Server IP:");
	JLabel lblPort = new JLabel("Port:");
	JButton btnConnect = new JButton("Connect");
	JPanel chatlog = new JPanel();
	JTextArea log = new JTextArea();
	JPanel main = new JPanel();
	
	public void init() {
		main.setLayout(new BorderLayout(0, 0));
		
		
		main.add(controls, BorderLayout.SOUTH);
		controls.setLayout(new GridLayout(2, 1, 0, 0));
		
		
		controls.add(chatstuff);
		chatstuff.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		message = new JTextField();
		chatstuff.add(message);
		message.setColumns(35);
		
		
		chatstuff.add(send);
		
		
		controls.add(conectionstuff);
		conectionstuff.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		
		conectionstuff.add(lblServer);
		
		server = new JTextField();
		conectionstuff.add(server);
		server.setColumns(15);
		
		
		conectionstuff.add(lblPort);
		
		port = new JTextField();
		conectionstuff.add(port);
		port.setColumns(10);
		
		
		conectionstuff.add(btnConnect);
		
		
		main.add(chatlog, BorderLayout.CENTER);
		
		
		log.setRows(12);
		log.setColumns(35);
		chatlog.add(log);
		add(main);

	}
	

}

public class ChatClient extends ChatUI implements ActionListener {
	private Socket socket = null;
	private DataInputStream  console = null;
	private DataOutputStream streamOut = null;

	@SuppressWarnings("deprecation")
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
	}
	//public static void main(String args[]) {
	//	ChatClient client = null;
	//	if (args.length != 2)
	//		System.out.println("Usage: java ChatClient host port");
	//	else
	//		client = new ChatClient(args[0], Integer.parseInt(args[1]));
	//}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnConnect) {
			ChatClient chat = new ChatClient(server.getText(), Integer.parseInt(port.getText()));
			chat.start();
		}else if(e.getSource() == send) {
			//writeToServer();
		}
		
	}
	
}
