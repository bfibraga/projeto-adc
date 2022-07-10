package pt.unl.fct.di.adc.silvanus.resources;

import com.google.cloud.storage.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@SuppressWarnings("serial")
@MultipartConfig
public class MediaResourceServlet extends HttpServlet {

	  /**
	   * Retrieves a file from GCS and returns it in the http response.
	   * If the request path is /gcs/Foo/Bar this will be interpreted as
	   * a request to read the GCS file named Bar in the bucket Foo.
	   */
	  @Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	        // Download file from a specified bucket. The request must have the form /gcs/<bucket>/<object>
	    	Storage storage = StorageOptions.getDefaultInstance().getService();
	        // Parse the request URL
	    	Path objectPath = Paths.get(req.getPathInfo());
	        if ( objectPath.getNameCount() < 2 ) {
	          throw new IllegalArgumentException("The URL is not formed as expected.\n" +
					  "Received " + objectPath + "\n" +
	              "Expecting /gcs/<bucket>/<object>");
	        }
	        // Get the bucket and the object names
	    	String bucketName = objectPath.getName(0).toString();
	    	String srcFilename = "";

		  for (int i = 1; i < objectPath.getNameCount(); i++) {
			  srcFilename = srcFilename.concat(objectPath.getName(i).toString());
			  if (i != objectPath.getNameCount()-1){
				  srcFilename = srcFilename.concat("/");
			  }
		  }

			System.out.println(bucketName);
		  	System.out.println(srcFilename);

	        Blob blob = storage.get(BlobId.of(bucketName, srcFilename));
	        
	        // Download object to the output stream. See Google's documentation.
	        resp.setContentType(blob.getContentType());
	        blob.downloadTo(resp.getOutputStream());
	  }

	  @Override
	  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
	        // Upload file to specified bucket. The request must have the form /gcs/<bucket>/<object>
	/*    	Path objectPath = Paths.get(req.getPathInfo());
	        if ( objectPath.getNameCount() < 2 ) {
				throw new IllegalArgumentException("The URL is not formed as expected.\n" +
						"Received " + objectPath + "\n" +
						"Expecting /gcs/<bucket>/<object>");
	        }
	        // Get the bucket and object from the URL 
	    	String bucketName = objectPath.getName(0).toString();
		  String srcFilename = "";

		  for (int i = 1; i < objectPath.getNameCount(); i++) {
			  srcFilename = srcFilename.concat(objectPath.getName(i).toString());
			  if (i != objectPath.getNameCount()-1){
				  srcFilename = srcFilename.concat("/");
			  }
		  }
			//Get content of the file
		  	InputStream	filecontent = req.getInputStream();
		  OutputStream out = resp.getOutputStream();

		  /*int read = 0;
			  final byte[] bytes = new byte[1024];

			  while ((read = filecontent.read(bytes)) != -1) {
				  out.write(bytes, 0, read);
			  }

		  if (out != null) {
			  out.flush();
			  out.close();
		  }
		  if (filecontent != null) {
			  filecontent.close();
		  }*/
		  /*
	    	// Upload to Google Cloud Storage (see Google's documentation)
		  Storage storage = StorageOptions.getDefaultInstance().getService();
	        BlobId blobId = BlobId.of(bucketName, srcFilename);

 	        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(req.getContentType()).build();
	        // The following is deprecated since it is better to upload directly to GCS from the client
	        Blob blob = storage.create(blobInfo, req.getInputStream());
			//resp.getWriter().println("File Uploaded. Number of bytes: " + filecontent);
*/
		  resp.setContentType("text/html;charset=UTF-8");

		  // Create path components to save the file
		  Path objectPath = Paths.get(req.getPathInfo());
		  String bucketName = objectPath.getName(0).toString();

		  String path = req.getParameter("destination");
		  Part filePart = req.getPart("file");
		  String fileName = req.getParameter("filename");
		  String fileType = filePart.getContentType();

		  String srcFilename = String.format("%s/%s", path, fileName);

		  InputStream filecontent = filePart.getInputStream();
		  ServletOutputStream out = resp.getOutputStream();

		  // Upload to Google Cloud Storage (see Google's documentation)
		  Storage storage = StorageOptions.getDefaultInstance().getService();
		  BlobId blobId = BlobId.of(bucketName, srcFilename);

		  BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
				  .setContentType(filePart.getContentType())
				  .build();
		  // The following is deprecated since it is better to upload directly to GCS from the client
		  Blob blob = storage.create(blobInfo, filecontent);
		  blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
		  out.println("File Uploaded to " + srcFilename);
	  }
}