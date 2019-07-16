// To save as "<TOMCAT_HOME>\webapps\hello\WEB-INF\classes\QueryServlet.java".

//[START gcs_imports]
/**import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;**/
//[END gcs_imports]

/* 
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
 */
import com.google.common.base.Preconditions;
import java.io.*;
import java.util.*;
import java.net.*;
import java.net.URI;
import java.util.concurrent.Future;
import java.nio.file.Paths;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
//import java.nio.channels.Channels;
//import org.joda.time.format.DateTimeFormatter;
//import org.joda.time.DateTimeZone;
//import org.joda.time.DateTime;
//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.FileOutputStream;
import javax.servlet.ServletContext;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.common.scopes.SimpleContainerScope;

public class RequestsServlet extends HttpServlet
{  
	private final WebSocketClient webSocketClient;
  	private final ClientSocket clientSocket;
  	public EchoServlet echoServlet;
  	
	private static final String ENDPOINT = "/echo";
  	private static final String WEBSOCKET_PROTOCOL_PREFIX = "ws://";
  	private static final String WEBSOCKET_HTTPS_PROTOCOL_PREFIX = "wss://";
  	private static final String APPENGINE_HOST_SUFFIX = ".appspot.com";

  	// GAE_INSTANCE environment is used to detect App Engine Flexible Environment
  	private static final String GAE_INSTANCE_VAR = "GAE_INSTANCE";
  	// GOOGLE_CLOUD_PROJECT environment variable is set to the GCP project ID on App Engine Flexible.
  	private static final String GOOGLE_CLOUD_PROJECT_ENV_VAR = "GOOGLE_CLOUD_PROJECT";
  	// GAE_SERVICE environment variable is set to the GCP service name.
  	private static final String GAE_SERVICE_ENV_VAR = "GAE_SERVICE";
  
  	public RequestsServlet() 
  	{
    	this.webSocketClient = createWebSocketClient();
    	this.clientSocket = new ClientSocket();
  	}
  	
	  // Given a node, the goal is to receive a feature file and read its contents
 	  public void doGet(HttpServletRequest req, HttpServletResponse res)
         throws IOException, ServletException 
        {
        	if (!webSocketClient.isRunning()) 
       		{
        		try 
        		{
        			webSocketClient.start();
         		 } 
				catch (Exception e) 
				{
				 e.printStackTrace();
				}
       		 } // end if webSocketClient not running
       		try
       		{ 
		    ClientUpgradeRequest request = new ClientUpgradeRequest();
		    // Attempt connection
		    Future<Session> future = webSocketClient.connect(clientSocket,new URI(getWebSocketAddress()), request);
		    // Wait for Connect
		    Session serverSession = future.get()
           //  try(ServerSocket serverSocket = new ServerSocket())
//             {
            System.out.println("The server is waiting to get more features ...");
    		//Try accepting client connections
    		String destUri=getWebSocketAddress(); 
    		URI echoUri = new URI(destUri);
    		webSocketClient.connect(echoServlet,echoUri,request);
    		System.out.printf("Connecting to : %s%n",echoUri);
    		}
    		catch (Exception e) 
			{
			    e.printStackTrace();
		    }
        } // End of the doGet Method
            
       public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException 
       {
       } // end of the doPost Method
    	 
    	 // String message = request.getParameter("message");
//     	 try 
//     	 {
//            sendMessageOverWebSocket(message);
//            response.sendRedirect("/");
//     	  } 
//     	 catch (Exception e) 
//     	 {
//            e.printStackTrace(response.getWriter());
//            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
//           }
        
        
 //      private void sendMessageOverWebSocket(String message) throws Exception 
//       {
//        if (!webSocketClient.isRunning()) 
//        {
//         try 
//         {
//         webSocketClient.start();
//          } 
//         catch (URISyntaxException e) 
//         {
//          e.printStackTrace();
//         }
//        } // end if webSocketClient not running
// 		  ClientUpgradeRequest request = new ClientUpgradeRequest();
// 		  // Attempt connection
// 		  Future<Session> future = webSocketClient.connect(clientSocket,new URI(getWebSocketAddress()), request);
// 		  // Wait for Connect
// 		  Session session = future.get();
// 		  // Send a message
// 		  session.getRemote().sendString(message);
// 		  // Close session
// 		  session.close();
//       }     
//       
      
       private WebSocketClient createWebSocketClient() 
       {
    	 WebSocketClient webSocketClient;
    	 if (System.getenv(GAE_INSTANCE_VAR) != null) 
    	 {
      		// If on HTTPS, create client with SSL Context
         SslContextFactory sslContextFactory = new SimpleContainerScope(WebSocketPolicy.newClientPolicy()).getSslContextFactory();
         webSocketClient = new WebSocketClient(sslContextFactory);
         } 
         else 
         {
      		// local testing on HTTP
      	    webSocketClient = new WebSocketClient();
          }
         return webSocketClient;
       }
   
   
   		/**
  		 * Returns the host:port/echo address a client needs to use to communicate with the server.
   		 * On App engine Flex environments, result will be in the form wss://project-id.appspot.com/echo
  		 */
  		public static String getWebSocketAddress() 
  		{
    		// Use ws://127.0.0.1:8080/echo when testing locally
    		String webSocketHost = "127.0.0.1:8080";
    		String webSocketProtocolPrefix = WEBSOCKET_PROTOCOL_PREFIX;

    		// On App Engine flexible environment, use wss://project-id.appspot.com/echo
    		if (System.getenv(GAE_INSTANCE_VAR) != null) 
    		{
      			String projectId = System.getenv(GOOGLE_CLOUD_PROJECT_ENV_VAR);
      			if (projectId != null) 
      			{
        			String serviceName = System.getenv(GAE_SERVICE_ENV_VAR);
       			    webSocketHost = serviceName + "-dot-" + projectId + APPENGINE_HOST_SUFFIX;
      			}
      			Preconditions.checkNotNull(webSocketHost);
      			// Use wss:// instead of ws:// protocol when connecting over https
      			webSocketProtocolPrefix = WEBSOCKET_HTTPS_PROTOCOL_PREFIX;
   			 }
    		return webSocketProtocolPrefix + webSocketHost + ENDPOINT;
  		} // end of getWebSocketAddress
  
        
}// end of RequestsServlet class

