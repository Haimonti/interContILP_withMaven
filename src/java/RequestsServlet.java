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
        response.setContentType("text/html");
        String filename = "/WEB-INF/feature_v1a.pl";
         ServletContext context = getServletContext();
		// First get the file InputStream using ServletContext.getResourceAsStream()
        // method.
        InputStream is = context.getResourceAsStream(filename);
        if (is != null) 
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            PrintWriter writer = response.getWriter();
            writer.println("<html>");
         	writer.println("<head>");
         	writer.println("<title>Requests Servlet</title>");  
         	writer.println("</head>");
         	writer.println("<body>");
            String text;
            writer.println("Here is the feature file stored on the server");
            writer.println("</br>");
            writer.println("</br>");
			// We read the file line by line and later will be displayed on the
            // browser page.
            while ((text = reader.readLine()) != null) 
            {
                writer.println(text + "</br>");
            }
            writer.println("</body>");
         	writer.println("</html>");
        }
        } // End of the doGet Method
  

}// end of QueryServlet class