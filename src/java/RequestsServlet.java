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

public class RequestsServlet extends HttpServlet
{  
	// Given a node, the goal is to receive a feature file and read its contents
 	  public void doGet(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException 
        {
        	ExecutorService pool=null;
            try(ServerSocket serverSocket = new ServerSocket(59090))
            {
            System.out.println("The server is waiting to get more features ...");
            pool = Executors.newFixedThreadPool(20);
            while (true) 
            {
                pool.execute(new Reader(serverSocket.accept()));
            }
            }
        	//Socket clientSocket = serverSocket.accept();
        } // End of the doGet Method
            
        public class Reader implements Runnable
        {
            private Socket socket;
            // This is the constructor to the class
            Reader (Socket clientSocket)
            {
             	this.socket = clientSocket;
            }
            	 
            public void run() 
        	{
              System.out.println("Connected: " + socket);	 
              try
              {
              
              //Try to get the feature file from client
              
              
              // Send its current file to the client
              PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    		  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			  out.println("Sending feature_v1a.pl to client ...");
			
			  String filename = "/WEB-INF/feature_v1a.pl";
         	  ServletContext context = getServletContext();
			  // First get the file InputStream using ServletContext.getResourceAsStream()
        	  // method.
        	  InputStream is = context.getResourceAsStream(filename);
        	  if (is != null) 
        	  {
				  InputStreamReader isr = new InputStreamReader(is);
				  BufferedReader reader = new BufferedReader(isr);
				  //PrintWriter writer = response.getWriter();
				  String text;
				  // We read the file line by line and later will be displayed on the
				  // browser page.
				  while ((text = reader.readLine()) != null) 
				  {
					out.println(text);
				  }
       	   	  }
       	   	  }
       	   	  catch(Exception e)
       	   	  {
       	   	   e.printStackTrace();
       	   	  }
       	   	  finally 
       	   	  {
                try 
                { 
                 socket.close(); 
                } 
                catch (IOException e) {}
                System.out.println("Closed: " + socket);
              }
       	   }// End of run() method
       	   
       	   } // End of Reader class
}// end of RequestsServlet class

