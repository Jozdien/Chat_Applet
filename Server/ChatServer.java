import java.io.*;
import java.net.*;

/** The server class for the chatroom. This class is the main driver that sets up
* the socket and fires a new Thread when a client connects. Internally it maintains a 
* synchronized buffer to avoid race conditions between clients. Any incoming traffic
* from any client is echoed to all clients, including the originator. 
*/
public class ChatServer
{	/** This is the driver for the server. It sets up a socket, and then fires threads for 
	*	each connect. 
	*/
	public static void main(String[] args ) 
	{	int i;
		int socketNumber = 4440;	
		if (args.length > 0)
		{	size = Integer.parseInt(args[0]);
		}
		if (args.length > 1)
		{	socketNumber = Integer.parseInt(args[1]);
		}
		try 
		{	ServerSocket s = new ServerSocket(socketNumber);
			for(i= 0; i < size; ++i)
			{	sessions[i] = null;
			}		
			new Echoer().start();
			while(true)
			{	Socket incoming = s.accept( );
				boolean found = false;
				int numusers = 0;
				int usernum = -1;
				synchronized(sessions)
				{	for(i = 0; i < size; ++i)
					{	if(sessions[i] == null)
						{	if(!found)
							{	sessions[i] = new PrintStream(incoming.getOutputStream());
								new ChatHandler(incoming, i).start();
								found = true;
								usernum = i;
								//System.out.println("assign "+i);
							}
						}
						else numusers++;
					}
					if(!found)
					{	PrintStream temp = new PrintStream(incoming.getOutputStream());
						temp.println("\n No available entry.  Disconnecting. Sorry. \n");
						temp.println("\n You must reload to try again. \n");
						temp = null; // Permit garbage collection of the PrintStream.
						s.close();
					}
					else
					{	sessions[usernum].println("\nThere are "+numusers+" other users.\n");
					}
				}
			}	
		}
		catch (Exception e) 
		{	System.err.println("Error in main: " + e);
		} 
	} 
 
	private static int size = 4;

	// The following are shared variables.  We use innerclasses and make these private. 
	// This greatly adds to the safety.  
	
	private static PrintStream [] sessions = new PrintStream[size];
	private static Buffer message = new Buffer();


	private static class ChatHandler extends Thread  // Several Writers of message. 
	{	private Socket incoming;
		private int counter;
		ChatHandler(Socket i, int c) { incoming = i; counter = c; }
		
		public void run()
		{	try 
			{	BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
				String name = "";
				synchronized(ChatServer.sessions)
				{	PrintStream out = ChatServer.sessions[counter];
					out.println("Enter your name: ");
					name = in.readLine();
					out.println( "Hello there, "+name+". Enter your name to exit.\n" );
				}
				boolean done = false;
				while (!done)
				{	String str = in.readLine();
					if (str == null)
					{	done = true;
					}
					else
					{	ChatServer.message.set("(" + name + "): " + str + "\r");
						if (str.trim().equals(name)) 
						{	
							ChatServer.message.set(name + " is going on a journey far away now.");
							done = true;
						}
					} 
				}
				incoming.close();			
			}
			catch (Exception e) 
			{	System.err.println("ChatHandler error: " + e);
			} 
			synchronized(ChatServer.sessions)
			{	ChatServer.sessions[counter].close();
				ChatServer.sessions[counter] = null;	
			}
		} 
	}// class ChatHandler

	private static class Echoer extends Thread // One Reader of message.
	{	// Broadcasts all messages to all users. 
		public void run()
		{	while(true)
			{	String s = ChatServer.message.get();
				synchronized(ChatServer.sessions)
				{	for(int i = 0; i < ChatServer.size; ++i)
						if(ChatServer.sessions[i] != null)
							ChatServer.sessions[i].println(s);
				}
			}
		}
	} // class Echoer

	private static class Buffer  // Implements a shared one element queue.  
	{	private String message = null;
		public synchronized void set(String s)
		{	try
			{	while(message  != null) 
					wait();
			}
			catch(InterruptedException e) {}
			message = s;		
			notify();
		}
		
		public synchronized String get()
		{	try
			{	while( message == null)
					wait();			
			}
			catch(InterruptedException e) {return null;}
			String result = message;
			message = null;
			notify();
			return result;
		}
	} // class Buffer
	
}
