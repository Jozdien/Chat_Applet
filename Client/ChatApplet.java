/* 
<applet code="ChatApplet" port=3000 width=300 height=400>
</applet>
*/
  
import java.awt.*;
import java.applet.Applet;
import java.net.*;
import java.io.*;
import java.awt.event.*;
/** ChatApplet is the main client class for the chat room. It manages all user communication, 
*	sends client strings to the server, and displays results from the server. 
*/
public class ChatApplet extends Applet implements Runnable
{
	/** Initialize the applet and set up the GUI.
	*/
	public void init() 
	{	setLayout(new BorderLayout());   
		messages = new TextArea();
		messages.setFont(new Font("Courier", Font.PLAIN, 10));
		add("Center", messages);
		
		send = new Button("Send a Line of Text");
		Sender sender = new Sender();
		send.addActionListener(sender);
		add("South", send);
		
		entry = new TextField();
		entry.addActionListener(sender);
		add("North", entry);

		messages.setText("Chat contents displayed here.");
		try
		{	
			URL server = getDocumentBase();
			int port = Integer.parseInt(getParameter("port"));
			connection = new Socket(server.getHost(),port);
			inputStream = new BufferedReader(new InputStreamReader( connection.getInputStream()) );
			outputWriter = new PrintWriter( connection.getOutputStream(),true);
    		}
		catch(UnknownHostException e){messages.append("No Host.");}
    	catch(IOException e){messages.append("Startup I/O Error: " +e);}
    		
    	new Thread(this).start();
   		entry.setText("Type your messages here.");
   		entry.selectAll();
	 	entry.requestFocus();
		setVisible(true);
	}
	
	/** Return the size of the frame in which the applet is displayed
	*	@return The size of the Frame.
	*/
	public Dimension setSize() {return new Dimension(600, 400);}
	
	/** This method executes in a separate thread. It continuously looks for server
	*	communication and displays anything it receives.
	*/
	public void run()
	{	try
		{	while (true)
			{	String fromServer = inputStream.readLine();
				if (fromServer != null)
				{	fromServer += '\n' ;
					messages.append(fromServer);
					entry.requestFocus();
				}
				else break;
			}
    		}
    		catch(IOException e){messages.append("Fetch Error: " +e);}
	}

	/** Retrieve information about the applet
	*	@return The information String. 
	*/
	public String getAppletInfo()
	{	return "JBChat v0.2, Joseph Bergin";
	}
	
	/** Break the communication link.
	*/
	public void destroy()
	{	try
		{	outputWriter.println("BYE");
			connection.close();
		}
		catch(IOException e){}
	}
		
	private TextField entry;
	private TextArea messages ;
	private Button send;
	private BufferedReader inputStream;
	private PrintWriter outputWriter;
	private Socket connection;
	
	private class Sender implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{	// Only two sources with identical actions
			String s = entry.getText();
			if(s != null && s.trim().length()>0)
			{	outputWriter.println(s);
				entry.setText("");
			}
			entry.requestFocus();
		}
	}
	
}