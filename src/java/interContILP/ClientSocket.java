package interContILP;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
//import org.eclipse.jetty.websocket.api.WebSocketListener;

/**
 * Basic Echo Client Socket.
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class ClientSocket  
{
    //private Logger logger = Logger.getLogger(interContILP.ClientSocket.class.getName());
    private Session session;
    // stores the messages in-memory.
    // Note : this is currently an in-memory store for demonstration,
    // not recommended for production use-cases.
    private static Collection<String> messages = new ConcurrentLinkedDeque<>();
    
      @OnWebSocketClose
      public void onWebSocketClose(int statusCode, String reason) {
	  //logger.fine("Connection closed: " + statusCode + ":" + reason);
	  System.out.println("Connection closed: "+statusCode+ ":" + reason);
	  this.session = null;
      }

      @OnWebSocketConnect
      public void onWebSocketConnect(Session session)
      {
	  //this.session = session;
	  //System.out.println("Got connect:", session);
	  this.session = session;
	  try
	  {
	    Future<Void> fut;
	    fut = session.getRemote().sendStringByFuture("Hello");
	    fut.get(2, TimeUnit.SECONDS); // wait for send to complete.

	    fut = session.getRemote().sendStringByFuture("Thanks for the conversation.");
	    fut.get(2, TimeUnit.SECONDS); // wait for send to complete.
	  }
	  catch (Throwable t)
	  {
	    t.printStackTrace();
	  }
      }

        @OnWebSocketMessage
        public void onMessage(String msg)
        {
	    //logger.fine("Message Received : " + msg);
	  System.out.println("Message Received :"+msg);
	  messages.add(msg);
	}

        //@OnWebSocketError
	/**public void onError(Throwable cause)
        {
	 cause.printStackTrace(System.err);
	 }*/
    
    // Retrieve all received messages.
    public static Collection<String> getReceivedMessages()
    {
	return Collections.unmodifiableCollection(messages);
    }
}
