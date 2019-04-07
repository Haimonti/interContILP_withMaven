// To save as "<TOMCAT_HOME>\webapps\hello\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.file.Paths;

import javax.servlet.*;
import javax.servlet.http.*;

//import org.apache.commons.fileupload.FileItem;
//import org.apache.commons.fileupload.FileUploadException;
//import org.apache.commons.fileupload.disk.DiskFileItemFactory;
//import org.apache.commons.fileupload.servlet.ServletFileUpload;
//import org.apache.commons.io.output.*;

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
      	//DiskFileItemFactory factory = new DiskFileItemFactory();   
      	// Create a new file upload handler
        //ServletFileUpload upload = new ServletFileUpload(factory); 
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
         //List fileItems = upload.parseRequest(request);
		 Part filePart = request.getPart("uploadFile");
		 // MSIE fix.
		 String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); 
		 InputStream fileContent = filePart.getInputStream();
    	 
         // Process the uploaded file items
         //Iterator i = fileItems.iterator();
		 System.out.println("File name is: "+fileName);
         out.println("<html>");
         out.println("<head>");
         out.println("<title>Query Servlet</title>");  
         out.println("</head>");
         out.println("<body>");
         out.println("Uploaded Filename: " + fileName + "<br>");
         // Write the file
		 fileName=savePath + File.separator + fileName;
		 filePart.write(fileName) ;
		 out.println("Writing File Done!");   
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