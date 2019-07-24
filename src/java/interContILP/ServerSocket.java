package interContILP;
import java.io.IOException;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/*
 * Server-side WebSocket : echoes received message back to client.
 */
public class ServerSocket extends WebSocketAdapter
{
    private Logger logger = Logger.getLogger(SendServlet.class.getName());
    
      @Override
      public void onWebSocketConnect(Session session) {
	  super.onWebSocketConnect(session);
	  logger.fine("Socket Connected: " + session);
      }

      @Override
      public void onWebSocketText(String message) {
	  super.onWebSocketText(message);
	  logger.fine("Received message: " + message);
	  try {
	      // echo message back to client
	      getRemote().sendString(message);
	  } catch (IOException e) {
	      logger.severe("Error echoing message: " + e.getMessage());
	  }
      }

      @Override
      public void onWebSocketClose(int statusCode, String reason) {
	  super.onWebSocketClose(statusCode, reason);
	  logger.fine("Socket Closed: [" + statusCode + "] " + reason);
      }

      @Override
      public void onWebSocketError(Throwable cause) {
	  super.onWebSocketError(cause);
	  logger.severe("Websocket error : " + cause.getMessage());
      }
}
