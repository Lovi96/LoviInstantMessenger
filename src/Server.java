import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Lovi on 2017. 03. 08. @ 23:19.
 */
public class Server extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	private JTextField userNameField;
	private String actualUserName;
	private JLabel userNameLabel;
	private JButton userNameSetter;
	private JScrollPane scrollPane;
	private JPanel panel1;
	private JPanel panel2;
	private Container contentPane;

	public Server() {
		super("Lovi's Instant Messenger");
		userText = new JTextField();
		userText.setEditable(false);
		setMinimumSize(new Dimension(300,150));
		setMaximumSize(new Dimension(900,450));
		userText.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						sendMessage(e.getActionCommand());
						userText.setText("");
					}
				}
		);
		chatWindow = new JTextArea();
		userNameSetter = new JButton("Set");
		userNameSetter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUserName(userNameField.getText());
			}
		});
		userNameField = new JTextField("Enter username");
		userNameField.setEditable(true);
		userNameLabel = new JLabel("Current username: Client");
		userNameField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUserName(e.getActionCommand());
				userNameField.setText("Username set!");

			}
		});
		scrollPane = new JScrollPane(chatWindow);
		scrollPane.setPreferredSize(new Dimension(250,160));
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1,BoxLayout.PAGE_AXIS));
		panel1.add(Box.createRigidArea(new Dimension(0,5)));
		panel1.add(userNameLabel);
		panel1.add(Box.createRigidArea(new Dimension(0,5)));
		panel1.add(scrollPane);
		//panel1.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		//panel1.add(Box.createRigidArea(new Dimension(0,5)));
		panel1.add(userText);
		panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2,BoxLayout.LINE_AXIS));
		panel2.add(userNameField);
		userNameField.setFocusable(true);
		userNameField.setRequestFocusEnabled(true);
		userNameField.requestFocus();
		panel2.add(userNameSetter);
		contentPane = getContentPane();
		contentPane.add(panel2,BorderLayout.NORTH);
		contentPane.add(panel1,BorderLayout.CENTER);
		//add(contentPane);
		setSize(600, 300);
		setVisible(true);
	}


	public void startRunning() {
		try {
			server = new ServerSocket(6789, 100);
			while(true) {
				try {
					waitForConnection();
					setupStreams();
					whileChatting();
				} catch(EOFException eof) {
					eof.printStackTrace();
					showMessage("\n Server ended the connection! ");
				} finally {
					closeCrap();
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}

	}

	private void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect... \n");
		connection = server.accept();
		showMessage("Now Connected to " + connection.getInetAddress().getHostName());
	}

	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup! \n");
		setButtonVisible(false);
	}

	private void setButtonVisible(boolean visible) {
		userNameSetter.setVisible(visible);
	}

	private void whileChatting() throws IOException {
		String message = "You are now connected!  ";
		sendMessage(message);
		ableToType(true);
		userNameField.setEditable(false);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch(ClassNotFoundException clsnfe) {
				clsnfe.printStackTrace();
				showMessage(" idk wtf that user sent!");
			}
		} while(!message.equals("CLIENT - END"));
	}

	private void closeCrap() {
		showMessage("\nClosing connections!\n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
			setButtonVisible(true);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void sendMessage(String message) {
		try {
			output.writeObject(actualUserName+" - " + message);
			output.flush();
			showMessage("\n"+actualUserName+" - " + message);
		} catch(IOException ioe) {
			chatWindow.append("\n Couldnt send message!!");
		}
	}

	private void showMessage(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(text);
			}
		});
	}

	private void ableToType(final boolean able) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(able);
			}
		});
	}

	private void setUserName(final String actionCommand) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				actualUserName = actionCommand;
				userNameLabel.setText("Current username: "+actualUserName);

			}
		});
	}
}
