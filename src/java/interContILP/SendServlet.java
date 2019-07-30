package interContILP;
import com.google.common.base.Preconditions;
import java.io.*;
import java.io.IOException;
import org.eclipse.jetty.util.IO;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.net.URISyntaxException;
import java.util.concurrent.Future;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.HttpResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.*;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.common.scopes.SimpleContainerScope;

@WebServlet("/send")
/** Servlet that sends the message sent over POST to over a websocket connection. */
public class SendServlet extends HttpServlet {

    //private Logger logger = Logger.getLogger(interContILP.SendServlet.class.getName());

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

    private final HttpClient httpClient;
    private final WebSocketClient webSocketClient;
    private final  ClientSocket clientSocket;

    public SendServlet()
    {
	//System.out.println("Does it enter the constructor?");
	this.httpClient = createHttpClient();
	this.webSocketClient = createWebSocketClient();
	this.clientSocket = new ClientSocket();
     }

      @Override
      public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	  String message = request.getParameter("message");
	  try
	  {
	      sendMessageOverWebSocket(message);
	      response.sendRedirect("/");
	  }
	  catch (Exception e)
	   {
	      //logger.severe("Error sending message over socket: " + e.getMessage());
	      e.printStackTrace(response.getWriter());
	      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
	  }
      }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
	String message = request.getParameter("message");
       	PrintWriter writer = response.getWriter();
	writer.println("<!DOCTYPE html>");
	writer.println("<html>");
	writer.println("<head>");
	writer.println("<title>Message Received</title>");
	writer.println("</head>");
	writer.println("<body>");
	writer.println("Message received is "+message);
	writer.println("</body>");
	writer.println("</html>");	
    }

    public HttpClient createHttpClient()
    {
	HttpClient httpClient;
	if (System.getenv(GAE_INSTANCE_VAR) != null) {
	    // If on HTTPS, create client with SSL Context
	    SslContextFactory sslContextFactory = new SslContextFactory();
	    httpClient = new HttpClient(sslContextFactory);
	} else {
	    // local testing on HTTP
	    httpClient = new HttpClient();
	}
	return httpClient;
    }

    public WebSocketClient createWebSocketClient() {
	return new WebSocketClient(this.httpClient);
    }

    public void sendMessageOverWebSocket(String message) throws Exception
    {
	//this.httpClient=creatHttpClient();
	//this.webSocketClient=createWebSocketClient();
	//this.clientSocket = new ClientSocket();
	//ServerSocket svrSkt = new ServerSocket();
	if (!httpClient.isRunning()) {
	    try {
		httpClient.start();
	    } catch (URISyntaxException e) {
		e.printStackTrace();
	    }
	}
	if (!webSocketClient.isRunning()) {
	    try {
		webSocketClient.start();
	    } catch (URISyntaxException e) {
		e.printStackTrace();
	    }
	}
	ClientUpgradeRequest request = new ClientUpgradeRequest();
	// Attempt connection
	Future<Session> future = webSocketClient.connect(clientSocket,new URI(getWebSocketAddress()), request);
	// Wait for Connect
	Session session = future.get();
	// Send a message
	//System.out.println("At server: "+message);
	// On connection the server sends the current feature file to the client
	// And requests it for its current feature file.
	//PrintWriter out = new PrintWriter(session.getRemote().getOutputStream(), true);
	//BufferedReader in = new BufferedReader(new InputStreamReader(session.getRemote().getInputStream()));
	//System.out.println("Sending feature_v1a.pl to client ...");

	String filename = "/WEB-INF/feature_v1a.pl";
	//ServletContext context = getServletContext();
	// First get the file InputStream using ServletContext.getResourceAsStream()
	// method.
	try
	{
	    // The client gets the file from the server
	    //ContentResponse response = httpClient.GET(getWebSocketAddress()+filename);
	    //System.out.println("Here are the contents of the file on the server "+response.getContentAsString());
	    // The client sends its file to the server
	    //
	    //ContentResponse sendToServer = httpClient.newRequest(getWebSocketAddress()+"/uploadFile")
	    //	.method(HttpMethod.POST)
	    //	.content(new InputStreamContentProvider(new FileInputStream("file_to_upload.txt")), "text/plain")
	    //	.send();
	    Request sendToServer = httpClient.newRequest(getWebSocketAddress()+filename);
	    //System.out.printf("Using HttpClient v%s%n", getHttpClientVersion());
	    System.out.printf("Requesting: ", getWebSocketAddress()+filename);
	    InputStreamResponseListener streamResponseListener = new InputStreamResponseListener();
	    sendToServer.send(streamResponseListener);
	    Response getFromServer  = streamResponseListener.get(5, TimeUnit.SECONDS);

	    if (getFromServer.getStatus() != HttpStatus.OK_200)
	    {
	      throw new IOException(String.format("Failed to GET URI [%d %s]: %s",getFromServer.getStatus(),getFromServer.getReason(),getWebSocketAddress()+filename));
	    }
	    Path tmpFile = Files.createTempFile("tmp", ".pl");

	    try (InputStream inputStream = streamResponseListener.getInputStream();OutputStream outputStream = Files.newOutputStream(tmpFile))
	    {
	      IO.copy(inputStream, outputStream);
	    }
	    System.out.printf("Downloaded %s%n", getWebSocketAddress()+filename);
	    System.out.printf("Destination: %s (%,d bytes)%n", tmpFile.toString(), Files.size(tmpFile));
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
	//session.getRemote().sendString(message);
	// Close session
	session.close();

	//return response.getContentAsString();
    }

    /**
     * Returns the host:port/echo address a client needs to use to communicate with the server.
     * On App engine Flex environments, result will be in the form wss://project-id.appspot.com/echo
     */
    public static String getWebSocketAddress() {
	// Use ws://127.0.0.1:8080/echo when testing locally
	String webSocketHost = "127.0.0.1:8080";
	String webSocketProtocolPrefix = WEBSOCKET_PROTOCOL_PREFIX;

	// On App Engine flexible environment, use wss://project-id.appspot.com/echo
	if (System.getenv(GAE_INSTANCE_VAR) != null) {
	    String projectId = System.getenv(GOOGLE_CLOUD_PROJECT_ENV_VAR);
	    if (projectId != null) {
		String serviceName = System.getenv(GAE_SERVICE_ENV_VAR);
		webSocketHost = serviceName + "-dot-" + projectId + APPENGINE_HOST_SUFFIX;
	    }
	    Preconditions.checkNotNull(webSocketHost);
	    // Use wss:// instead of ws:// protocol when connecting over https
	    webSocketProtocolPrefix = WEBSOCKET_HTTPS_PROTOCOL_PREFIX;
	}
	return webSocketProtocolPrefix + webSocketHost + ENDPOINT;
    }
}
