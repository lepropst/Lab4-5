import javax.swing.JApplet;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

public class ChatClient extends JApplet {
	private JTextField message;
	private JTextField server;
	private JTextField port;

	public ChatClient() {
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
