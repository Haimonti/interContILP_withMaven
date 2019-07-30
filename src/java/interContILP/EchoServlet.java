package interContILP;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import javax.servlet.annotation.WebServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/*
 * Server-side WebSocket registered as /echo servlet.
 */
//@WebServlet(name = "EchoServlet", urlPatterns = { "/echo" })
public class EchoServlet extends WebSocketServlet implements WebSocketCreator
{
      @Override
      public void configure(WebSocketServletFactory factory)
      {
	  factory.setCreator(this);
      }

     @Override
     public Object createWebSocket(ServletUpgradeRequest servletUpgradeRequest,ServletUpgradeResponse servletUpgradeResponse)
     {
	 return new ServerSocket();
     }
}
