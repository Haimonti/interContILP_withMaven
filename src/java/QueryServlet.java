// To save as "<TOMCAT_HOME>\webapps\hello\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.util.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;

 
public class QueryServlet extends HttpServlet 
{  
 	 /**
     	 * Name of the directory where uploaded files will be saved, relative to
     	 * the web application directory.
     	 */
    	private static final String SAVE_DIR = "uploadFiles";
    	public File file;
    	//public String filePath;
    	
   		public void doPost(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException 
     	{ 
      	// read form fields
        //String fName = request.getParameter("fname");
        // gets absolute path of the web application
        String appPath = request.getServletContext().getRealPath("");
        // constructs path of the directory to save uploaded file
        String savePath = appPath + File.separator + SAVE_DIR;
        // Allocate a output writer to write the response message into the network socket
      	PrintWriter out = response.getWriter();
      	DiskFileItemFactory factory = new DiskFileItemFactory();   
      	// Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory); 
        // creates the save directory if it does not exists
        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) 
        {
            fileSaveDir.mkdir();
        }
 		//Get the file(s)
 		try 
 		{ 
         // Parse the request to get file items.
         List fileItems = upload.parseRequest(request);
	
         // Process the uploaded file items
         Iterator i = fileItems.iterator();

         out.println("<html>");
         out.println("<head>");
         out.println("<title>Query Servlet</title>");  
         out.println("</head>");
         out.println("<body>");
   
      while ( i.hasNext () ) 
       {
		FileItem fi = (FileItem)i.next();
		if ( !fi.isFormField () ) 
		{
		   // Get the uploaded file parameters
		   String fieldName = fi.getFieldName();
		   String fileName = fi.getName();
		   System.out.println("File name is: "+fileName);
		   String contentType = fi.getContentType();
		   boolean isInMemory = fi.isInMemory();
		   long sizeInBytes = fi.getSize();
		   // Write the file
		   file=new File(savePath + File.separator + fileName);
		   fi.write(file) ;
		   out.println("Uploaded Filename: " + fileName + "<br>");
		   //Get the IP address of the machine from where the file was uploaded
		   String ip = request.getRemoteAddr();
		   if (ip.equalsIgnoreCase("0:0:0:0:0:0:0:1")) 
		   {
			 InetAddress inetAddress = InetAddress.getLocalHost();
			 String ipAddress = inetAddress.getHostAddress();
			 ip = ipAddress;
			 //Hostname
			 String hostname = inetAddress.getHostName();
			 out.println("Name of hostname : " + hostname+"<br>"); 
		   }
		out.println("The IP address from where the file was uploaded: " + ip + "<br>"); 
		out.println("*******************************************" + "<br>");
		out.println("Performing the union operation with local file"+ "<br>");
		//Runtime rt = Runtime.getRuntime ();
		/** Assume that the script we want to call resides in the scripts directory
			of our webapp for e.g. webapps/interContILP/WEB-INF/scripts
		*/
		//Process process = rt.exec ("../../scripts/union.sh");
		//System.out.println(" Start executing the shell scripts .....");	  
		     }
         }
         out.println("</body>");
         out.println("</html>");
         } 
         catch(Exception ex) 
         {
            System.out.println(ex);
         }
         finally 
      	{
         out.close();  // Always close the output writer
        }
      } // end of doPost method
}// end of QueryServlet class