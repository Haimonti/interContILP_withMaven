package interContILP;
import javax.servlet.annotation.WebServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/*
 * Server-side WebSocket registered as /echo servlet.
 */
//@WebServlet(name = "EchoServlet", urlPatterns = { "/echo" })
public class EchoServlet extends WebSocketServlet
{
      @Override
      public void configure(WebSocketServletFactory factory)
      {
	  factory.register(ServerSocket.class);
      }
}
