// To save as "<TOMCAT_HOME>\webapps\hello\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.file.Paths;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.FileOutputStream;

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
    	int DEFAULT_BUFFER_SIZE = 2048;
    	//public String filePath;
    	int isRead=0;
    	InputStream fileContent=null;
    	OutputStream outputContent=null;
   		public void doPost(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException 
     	{ 
        // gets absolute path of the web application
        String appPath = request.getServletContext().getRealPath("");
        // constructs path of the directory to save uploaded file
        //String savePath = appPath + File.separator + SAVE_DIR;
        String savePath=SAVE_DIR;
        // Allocate a output writer to write the response message into the network socket
      	PrintWriter out = response.getWriter();
        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) 
        {
            fileSaveDir.mkdir();
        }
        System.out.println("Path where file is to be stored: "+savePath);
 		//Get the file(s)
 		try 
 		{ 
         // Parse the request to get file items.
		 Part filePart = request.getPart("uploadFile");
		 // MSIE fix.
		 String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); 
		 out.println("<html>");
         out.println("<head>");
         out.println("<title>Query Servlet</title>");  
         out.println("</head>");
         out.println("<body>");
         out.println("Uploaded Filename: " + fileName + "<br>");
         out.println("<br>");
         out.println("<br>");
         System.out.println("File name is: "+fileName);
		 fileContent = filePart.getInputStream();
		 // Write the file
		 fileName=savePath + File.separator + fileName;		 
		 //File fNew = new File(fileName);
		 outputContent = new FileOutputStream(fileName);
	     while((isRead = fileContent.read())!=-1) 
		 {
		  outputContent.write(isRead);
		 }
		 outputContent.close();
		 fileContent.close();
 		 out.println("The uploaded file has been written on the server ....");  
 		 out.println("<br>");
 		 out.println("<br>");
 		 out.println("Calling the union script on the server ...."); 
 		 out.println("<br>");
 		 out.println("<br>");
 		 String scriptPath = "/software/yap-6.2.2/";
 		 String script = "union_features_v1.sh";
 		 String currFeatServer ="feature_server.pl";
 		 String uploadFeat = "../../uploadFiles/feature_local.pl";
 		 String outFile="feature_union_v1a.pl";
 		 try 
 		 {
        	//ProcessBuilder unionFeat = new ProcessBuilder("/bin/bash", scriptPath + script);
        	ProcessBuilder unionFeat = new ProcessBuilder();
        	System.out.println("Print the current directory "+unionFeat.directory());
        	// Set the working directory
        	unionFeat.directory(new File(System.getProperty("user.dir")+scriptPath));
        	System.out.println("Did it update the current directory? "+unionFeat.directory());
        	unionFeat=new ProcessBuilder("/bin/bash",unionFeat.directory()+File.separator+script,currFeatServer,uploadFeat,outFile);
        	unionFeat.redirectErrorStream(true);
        	Process pb = unionFeat.start();
        	System.out.println("Started the union script");
        	System.out.println("Trying to print input stream ...");
        	// this reads from the subprocess's input stream
            BufferedReader subPbInputReader = 
                new BufferedReader(new InputStreamReader(pb.getInputStream()));
			String line = null;
            while ((line = subPbInputReader.readLine()) != null)
                System.out.println(line);
            subPbInputReader.close();    
            // Get the output stream
            //OutputStream out = pb.getOutputStream(); 
      		// close the output stream 
            //System.out.println("Closing the output stream"); 
            //out.close();  	
        	//System.out.println(pb.getInputStream());
        	//System.out.println("Output stream is ...");
        	//System.out.println(pb.getOutputStream());
        	pb.waitFor();
   		 } 
   		 catch (Exception e) 
   		 {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
   		 }
 		 out.println("Done! Server now has the union of the uploaded feature file and its local feature file ...."); 
 		 out.println("<br>");
 		 out.println("<br>");
 		 out.println("Build a local model on the server ....");
 		 out.println("<br>");
 		 out.println("<br>");
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