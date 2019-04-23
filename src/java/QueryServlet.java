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

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.file.Paths;
import java.nio.channels.Channels;
import org.joda.time.format.DateTimeFormatter;
//import org.joda.time.DateTimeZone;
//import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.FileOutputStream;

public class QueryServlet extends HttpServlet 
{  
 	 /**
     	 * Name of the directory where uploaded files will be saved, relative to
     	 * the web application directory.
     	 */
    	private static final String SAVE_DIR = "uploadFiles";
    	//public File file;
    	int DEFAULT_BUFFER_SIZE = 2048;
    	//public String filePath;
    	int isRead=0;
    	InputStream fileContent=null;
    	OutputStream outputContent=null;
    	// Create a GCS Service with back-off parameters
     	/**private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
  			.initialRetryDelayMillis(10)
            .retryMaxAttempts(10)
            .totalRetryPeriodMillis(15000)
            .build());*/
          //Create buffer size    
        private static final int BUFFER_SIZE = 2 * 1024 * 1024;
          //Create a cloud storage bucket
		private final String bucket = "steel-earth-236015.appspot.com";
		//private static final String bucket=System.getenv("BUCKET_NAME");
		private static Storage storage = null;
   		//private final String appDeployPath="/home/haimonti/interContILP_withMaven/target/QueryServlet-1";  
  		
  		public void init() 
  		{
    		storage = StorageOptions.getDefaultInstance().getService();
 	    }
 	    
   		public void doPost(HttpServletRequest request, HttpServletResponse response)
         throws IOException, ServletException 
     	{ 
     	      	
        // gets absolute path of the web application
        String appPath = request.getServletContext().getRealPath("");
        //String appPath=System.getenv("PATH"))
        //ProcessBuilder appPath = new ProcessBuilder("/bin/bash", "echo $HOME");
        //appPath.directory(new File("/home/haimonti/interContILP_withMaven/target/QueryServlet-1"));
        System.out.println("The appPath where uploadFiles resides for now: "+appPath);
        // constructs path of the directory to save uploaded file
        String savePath = appPath + File.separator + SAVE_DIR;
        //String savePath = System.getProperty("user.dir") + File.separator + SAVE_DIR;
        //String savePath=File.separator+SAVE_DIR;
        // Allocate a output writer to write the response message into the network socket
      	PrintWriter outPage = response.getWriter();
        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) 
        {
            fileSaveDir.mkdir();
            System.out.println("Created new directory?");
        }
        System.out.println("Path where file is to be stored: "+savePath); 
 		//Get the file(s)
 		try 
 		{ 
         // Parse the request to get file items.
		 Part filePart = request.getPart("uploadFile");
		 // MSIE fix.
		 String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); 
		 // Extract filename
		 //String fileName = uploadedFilename(filePart); 
		 System.out.println("FileName is: "+fileName);
		 //Modify access list to allow all users with link to read file
   		 List<Acl> acls = new ArrayList<>();
    	 acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
   		 // the inputstream is closed by default, so we don't need to close it here
    	 Blob blob =
         storage.create(BlobInfo.newBuilder(bucket, fileName).setAcl(acls).build(),filePart.getInputStream());
  		 /**GcsFileOptions.Builder builder = new GcsFileOptions.Builder();
  		 // Set the file to be publicly viewable
		 builder.acl("public-read"); 
 		 GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
 		 System.out.println("Instance is: "+instance);
  		 GcsOutputChannel outputChannel;
  		 GcsFilename gcsFile = new GcsFilename(bucket, fileName);
  		 System.out.println("Bucket is: "+bucket);
  		 outputChannel = gcsService.createOrReplace(gcsFile, instance);
  		 copy(filePart.getInputStream(), Channels.newOutputStream(outputChannel)); **/
  		 System.out.println("Writing file to cloud storage .....");
  		 response.getWriter().print(blob.getMediaLink());
		 //return filename; // Return the filename without GCS/bucket appendage
		 outPage.println("<html>");
         outPage.println("<head>");
         outPage.println("<title>Query Servlet</title>");  
         outPage.println("</head>");
         outPage.println("<body>");
         outPage.println("Uploaded Filename: " + fileName + "<br>");
         outPage.println("<br>");
         outPage.println("<br>");
         System.out.println("File name is: "+fileName);
		 fileContent = filePart.getInputStream();
		 
		 //BlobInfo blobInfo =storage.create(BlobInfo.newBuilder(bucketName, fileName)
              // Modify access list to allow all users with link to read file
          //    .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER)))).build(),filePart.getInputStream());
         //Return public download link
         //String pubLink = blobInfo.getMediaLink();     
		 // Write the file
		 //outputChannel = gcsService.createOrReplace(fileName, instance);
    	 //copy(request.getInputStream(), Channels.newOutputStream(outputChannel));
		 fileName=savePath+File.separator + fileName;		 
		 File fNew = new File(fileName);
		 outputContent = new FileOutputStream(fNew);
		 if (!fNew.exists()) 
		 {
	      fNew.createNewFile();
	      System.out.println("Created new file?");
	  	  }
	     while((isRead = fileContent.read())!=-1) 
		 {
		  outputContent.write(isRead);
		 }
		 outputContent.flush();
		 outputContent.close();
		 fileContent.close(); 
 		 outPage.println("The uploaded file has been written on the server ....");  
 		 outPage.println("<br>");
 		 outPage.println("<br>");
 		 outPage.println("Calling the union script on the server ...."); 
 		 outPage.println("<br>");
 		 outPage.println("<br>");
 		 
 		 String scriptPath = "/software/yap-6.2.2/";
 		 String script = "union_features_v1.sh";
 		 
        	//ProcessBuilder unionFeat = new ProcessBuilder("/bin/bash", scriptPath + script);
        	ProcessBuilder unionFeat = new ProcessBuilder();
        	//System.out.println("Print the current directory "+unionFeat.directory());
        	// Set the working directory
        	//unionFeat.directory(new File("/home/haimonti/interContILP_withMaven/target/QueryServlet-1"));
        	// dirUnionFeat <-- /var/lib/jetty/webapps/root
        	unionFeat.directory(new File(System.getProperty("user.dir"))); //<---- /base/data/home
        	//unionFeat.directory(new File("/google/google-cloud-sdk"));
        	System.out.println("Current directory of unionFeat is: "+unionFeat.directory());
        	System.out.println("What is the PATH seen by the JAVA process? "+System.getenv("PATH"));
        	String currFeatServer =unionFeat.directory()+scriptPath+"feature_server.pl";
        	//String currFeatServer=bucket+scriptPath+"feature_server.pl";
 		 	//String uploadFeat = "../../uploadFiles/feature_local.pl";
 		 	String uploadFeat=savePath+File.separator+"feature_local.pl"; //<-- Works on DevApp Server
 		 	//String uploadFeat=bucket+File.separator+"feature_local.pl";
 		    String outFile=unionFeat.directory()+scriptPath+"feature_union_v1a.pl";
 		    //String outFile=bucket+scriptPath+"feature_union_v1a.pl";
 		    // you need a shell to execute a command pipeline
    		//List<String> commands = new ArrayList<String>();
    		//commands.add("/bin/bash");
    		//commands.add("-c");
    		//commands.add("cd /");
    		/**commands.add("cp " +currFeatServer+ " features.pl");
    		commands.add("cp " +uploadFeat + " features1.pl");
    		commands.add("shift");
 		    commands.add("./yap <<+");
 		    commands.add("consult('union_features_v1.pl').");
 		    commands.add("tell('features_union.pl'), union('features.pl','features1.pl'), told.");
 		    commands.add("+");
 		    commands.add("mv features_union.pl features.pl");
 		    commands.add("shift");
 		    commands.add("cat features.pl | grep -v " +"features from "+">"+outFile); */
 		    //unionFeat=new ProcessBuilder(commands);
 		    //unionFeat = new ProcessBuilder("/bin/bash", "cd /");
 		    //unionFeat=new ProcessBuilder("../../../../../../bin/bash","-c", bucket+scriptPath+script,currFeatServer,uploadFeat,outFile);
        	//unionFeat=new ProcessBuilder("/bin/bash",bucket+scriptPath+script,currFeatServer,uploadFeat,outFile);
        	unionFeat=new ProcessBuilder("/bin/bash",unionFeat.directory()+scriptPath+script,currFeatServer,uploadFeat,outFile);
        	unionFeat.redirectErrorStream(true);
        	Process pb = unionFeat.start();
        	System.out.println("Started the union script....");
        	System.out.println("Trying to print input stream ...");
        	
        	// this reads from the subprocess's input stream
            BufferedReader subPbInputReader = 
                new BufferedReader(new InputStreamReader(pb.getInputStream()));
			String line = null;
            while ((line = subPbInputReader.readLine()) != null)
                System.out.println(line);
            subPbInputReader.close();  
            
            // Get the output stream
            //OutputStream outSt = pb.getOutputStream(); 
            //outSt.close();
            
            // this reads from the subprocess's error stream
            //BufferedReader subPbErrorReader = 
            //    new BufferedReader(new InputStreamReader(pb.getErrorStream()));
			//String lineEr = null;
            //while ((lineEr = subPbErrorReader.readLine()) != null)
            //    System.out.println(lineEr);
            //subPbErrorReader.close();  
        
            // close the output stream 
            //System.out.println("Closing the output stream"); 
            //out.close();  	
        	//System.out.println(pb.getInputStream());
        	//System.out.println("Output stream is ...");
        	//System.out.println(pb.getOutputStream());
        	pb.waitFor();
        	
        	//Finally write the union file to the bucket
   		 
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
      
	/**public String uploadedFilename(Part part) 
	{
		final String partHeader = part.getHeader("content-disposition");
		for (String content : part.getHeader("content-disposition").split(";")) 
		{
    	if (content.trim().startsWith("fileName")) 
    	   {
			// Append a date and time to the filename
			 DateTimeFormatter dtf = DateTimeFormat.forPattern("-YYYY-MM-dd-HHmmssSSS");
			 DateTime dt = DateTime.now(DateTimeZone.UTC);
			 String dtString = dt.toString(dtf);
			final String fName =
			  dtString + content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			return fName;
    		}
  		}
  		return null; 
	}**/
	
	/**private GcsFilename getFileName(HttpServletRequest req) 
	{
    String[] splits = req.getRequestURI().split("/", 4);
    if (!splits[0].equals("") || !splits[1].equals("gcs")) 
    {
      throw new IllegalArgumentException("The URL is not formed as expected. " +
          "Expecting /gcs/<bucket>/<object>");
    }
    return new GcsFilename(splits[2], splits[3]);
  	} // End of getFileName method
  
  /**
   * Transfer the data from the inputStream to the outputStream. Then close both streams.
   */
  /**private void copy(InputStream input, OutputStream output) throws IOException 
  {
    try 
    {
      byte[] buffer = new byte[BUFFER_SIZE];
      int bytesRead = input.read(buffer);
      while (bytesRead != -1) 
      {
        output.write(buffer, 0, bytesRead);
        bytesRead = input.read(buffer);
      }
    } 
    finally 
    {
      input.close();
      output.close();
    }
   } // End of copy method **/

}// end of QueryServlet class