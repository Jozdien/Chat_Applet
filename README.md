# Running the applet
First, compile the server java code.
In the main directory,

`javac Server/ChatServer.java`

Then run the server, passing as arguments both the limit on the number of participants at a time, and the localhost port.

`java Server/ChatServer.java 4 3000`

where 4 is the maximum number of participants, and 3000 is the port number it can be accessed through.

Then, in a new terminal, run the following code in the main directory:

`telnet localhost 3000`

This should begin an instance of the chat, and running simultaneous such terminals will allow you to pass messages through the server.   

To run the messaging through an applet, first ensure that the functional dependancies have been met (primarily using Java 1.7 or lower).
Then, compile the applet java code in the main directory as,

`javac Client/ChatApplet.java`

Then view the applet by running the following code:

`appletviewer Client/ChatApplet.java`