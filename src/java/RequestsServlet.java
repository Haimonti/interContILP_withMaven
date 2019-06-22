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

import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.file.Paths;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
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

public class RequestsServlet extends HttpServlet 
{  
	  private final int ARBITARY_SIZE = 1048;
	  // source acts as the distributor of new messages 
	  MessageSource source = new MessageSource(); 
	  // socketClients holds references to all the socket-connected clients 
	  Vector socketClients = new Vector();
	  
	  // Given a node, the goal is to receive a feature file and read its contents
 	  public void doGet(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException 
        {
        response.setContentType("text/plain");
        response.setHeader("Content-disposition", "attachment; filename=feature_v1a.pl");
        
        //This servlet broadcasts that it is done with local computation
        String msg ="Done with local computation. Ready to receive features ....";
        //Broadcast this message to neighbors
        broadcastMessage(msg);
        
        try(InputStream in = request.getServletContext().getResourceAsStream("/WEB-INF/feature_v1a.pl");
          OutputStream out = response.getOutputStream()) 
          {
 		    byte[] buffer = new byte[ARBITARY_SIZE];
         	int numBytesRead;
            while ((numBytesRead = in.read(buffer)) > 0) 
            {
                out.write(buffer, 0, numBytesRead);
            }
            in.close();
            out.flush();
           }  
         catch(Exception e)
         {
         System.out.println(e);
         }      
} // End of the doGet Method
  
  	public void broadcastMessage(String message) 
  	{ 
  		source.sendMessage(message); 
  		// Directly send the message to all the socket-connected clients
  		Enumeration enum = socketClients.elements(); 
  		while (enum.hasMoreElements()) 
  		{ Socket client = null; 
  		  try 
  		  { 
  		    client = (Socket)enum.nextElement(); 
  		    PrintStream out = new PrintStream(client.getOutputStream());      
   			out.println(message); 
  		    }
  		     catch (IOException e) 
  		     { // Problem with a client, close and remote it 
  		     try 
				 { 
				 if (client != null) client.close(); 
				 } 
  		     catch (IOException ignored) 
				 { 
				 } 
  		     socketClients.removeElement(client); 
  		     } // end of catch 
  		  } // end of enum loop
  	}

}// end of RequestsServlet class