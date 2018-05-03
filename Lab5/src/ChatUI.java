import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class ChatUI extends JApplet {
		JTextField message = new JTextField();
		JTextField server = new JTextField();
		JTextField port = new JTextField("5555");
		JPanel chatstuff = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JPanel controls = new JPanel(new GridLayout(2, 1));
		JButton send = new JButton("Send");
		JPanel connectionstuff = new JPanel();
		JLabel lblserver = new JLabel("Server: ");
		JLabel lblport = new JLabel("Port: ");
		JButton btnConnect = new JButton("Connect");
		JPanel chatlog = new JPanel();
		JTextArea log = new JTextArea();
		JPanel main = new JPanel(new BorderLayout());
		
		
		public void init() {
			message.setColumns(35);
			chatstuff.add(message);
			
			chatstuff.add(send);
			controls.add(chatstuff);
			connectionstuff.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			controls.add(connectionstuff);
			
			connectionstuff.add(lblserver);
			
			server.setColumns(15);
			connectionstuff.add(server);
			connectionstuff.add(lblport);
			port.setColumns(10);
			connectionstuff.add(port);
			connectionstuff.add(btnConnect);
			
			main.add(chatlog, BorderLayout.CENTER);
			
			log.setRows(12);
			log.setColumns(35);
			chatlog.add(log);
			
			main.add(controls, BorderLayout.SOUTH);
			add(main);
			
			setSize(500, 500);
			
			
		}
		
		public void createDmFrame() {
			
		}
		
	
	

}