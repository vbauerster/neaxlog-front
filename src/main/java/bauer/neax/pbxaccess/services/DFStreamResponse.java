package bauer.neax.pbxaccess.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;

public class DFStreamResponse implements StreamResponse {
        private InputStream is;
//        private BufferedInputStream bis;
        
        private String filename;
		private String mime;

        public DFStreamResponse(InputStream is) {
                this.is = is;
//                bis = new BufferedInputStream (is);
        }

        
        public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}        
        
        public void setContentType(String mime){
        	this.mime = mime;
        }
        
        
        public String getContentType() {
                return mime;
//                "application/pdf";  
//                "text/csv";
        }

        public InputStream getStream() throws IOException {
        	return new BufferedInputStream(is);
        }

        public void prepareResponse(Response response) {

        	String ext = ".";
        	if(mime.endsWith("ms-excel"))
        		ext = ext + "xls";
        	else if (mime.endsWith("pdf")) 
        		ext = ext + "pdf";
        	else
        		ext = ext + "dat";
       
                response.setHeader("Content-Disposition", "attachment; filename=" + filename + ext);
                   
        }
}