package Client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client implements Runnable, ActionListener {

  // The client socket
  private static Socket clientSocket = null;
  // The output stream
  private static PrintStream outputStream = null;
  // The input stream
  private static DataInputStream inputStream = null;
  private static BufferedReader inputLine = null;
  private static boolean closed = false;
  // GUI Component Declaration
  private static JFrame frame = new JFrame();
  private static JPanel panel = new JPanel();
  private static JTextField textField = new JTextField();
  private static JTextArea messageArea = new JTextArea();
  private static JScrollPane scroll;

  public Client() {
    //Layout GUI
    // set the basic frame
    frame.setBounds(400, 400, 400, 400);
    frame.setTitle("Chatter");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(panel);
    frame.setVisible(true);
    // make form
    messageArea.setEditable(false);
    messageArea.setLineWrap(true);
    messageArea.setWrapStyleWord(true); 
  
    scroll = new JScrollPane(messageArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    panel.setLayout(new BorderLayout());
    panel.add(BorderLayout.NORTH, textField);
    panel.add(BorderLayout.CENTER, scroll);
    panel.add(messageArea);
    //add Listener
    textField.addActionListener(this);
    WindowListener listen = new WindowAdapter()
    {
      public void windowClosing(WindowEvent ev)
      { 
        System.exit(0);
      }
    };
  }

  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub
    //get and print textmessage on textfield
	 outputStream.println(textField.getText());
    textField.setText("");
  }

  public static void main(String[] args) {
    Client client = new Client();
    // The server port
    int portNumber = 2222;
    // The server host.
    // For now we are using localhost
    String host = "localhost";
    
    if (args.length < 2) {
      System.out
      .println("Usage: java MultiThreadChatClient <host> <portNumber>\n"
      + "Now using host=" + host + ", portNumber=" + portNumber);
    } else {
      host = args[0];
      portNumber = Integer.valueOf(args[1]).intValue();
    }
    
    /*
    * Open a socket on the defined host and port. Open input and output streams.
    */
    try {
      clientSocket = new Socket(host, portNumber);
      inputLine = new BufferedReader(new InputStreamReader(System.in));
      //Output Stream is connected to the Input Stream of the server
      outputStream = new PrintStream(clientSocket.getOutputStream());
      //Output Stream is connected to the Input Stream of the server
      inputStream = new DataInputStream(clientSocket.getInputStream());
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host " + host);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to the host "
      + host);
    }

    /*
    * If everything has been initialized then we want to write some data to the
    * socket we have opened a connection to on the port portNumber.
    */
    if (clientSocket != null && outputStream != null && inputStream != null) {
      try { 
        /* Create a thread to read from the server. */
        new Thread(new Client()).start();
        while (!closed) {
          outputStream.println(inputLine.readLine().trim());
        }
        /*
        * Close the output stream, close the input stream, close the socket.
        */
        outputStream.close();
        inputStream.close();
        clientSocket.close();
      } catch (IOException e) {
        System.err.println("IOException:  " + e);
      }
    }
  }

 @SuppressWarnings("deprecation")
 @Override
  public void run() {
    /*
    * Continuously read from the socket till we receive "/quit" command from the
    * server. Once we received that then we want to break.
    */
    String responseLine;
    
    // Process messages from server.
    try {
      while ((responseLine = inputStream.readLine()) != null) {
        messageArea.append(responseLine+"\n");
        if (responseLine.indexOf("*** Bye") != -1)
        break;
      }
      closed = true;
    } catch (IOException e) {
      System.err.println("IOException:  " + e);
    }
  }

}
